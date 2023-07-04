package ru.ServerRestApp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ServerRestApp.models.Invitation;
import ru.ServerRestApp.models.Person;
import ru.ServerRestApp.repositories.InvitationsRepository;
import ru.ServerRestApp.repositories.PeopleRepository;
import ru.ServerRestApp.repositories.TeamsRepository;
import ru.ServerRestApp.util.DataException;

import java.util.List;
import java.util.Optional;

@Service
public class InvitationsService {

    private final InvitationsRepository invitationsRepository;
    private final PeopleRepository peopleRepository;
    private final TeamsRepository teamsRepository;
    @Autowired
    public InvitationsService(InvitationsRepository invitationsRepository, PeopleRepository peopleRepository, TeamsRepository teamsRepository) {
        this.invitationsRepository = invitationsRepository;
        this.peopleRepository = peopleRepository;
        this.teamsRepository = teamsRepository;
    }


    @Transactional(readOnly = true)
    public Optional<Invitation> findById(int id) {
        return invitationsRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Invitation> findByIdFromAndIdTo(int id_from, int id_to) {
        return invitationsRepository.findByPersonFromIdAndPersonToId(id_from, id_to);
    }

    @Transactional(readOnly = true)
    public List<Invitation> findAll() {
        return invitationsRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Invitation> findByPersonFromId(int id) {
        return invitationsRepository.findByPersonFromId(id);
    }

    @Transactional(readOnly = true)
    public List<Invitation> findByPersonToId(int id) {
        return invitationsRepository.findByPersonToId(id);
    }

    @Transactional
    public void save(Invitation invitation) {
        if (invitation.getPersonFrom() != null)
            invitation.setPersonFrom(peopleRepository.findById(invitation.getPersonFrom().getId()).get());
        if (invitation.getPersonTo() != null)
            invitation.setPersonTo(peopleRepository.findById(invitation.getPersonTo().getId()).get());

        int id = invitationsRepository.save(invitation).getId();
        invitation.setId(id);
    }

    @Transactional
    public void update(Invitation invitation) {
        if (invitation.getPersonFrom() != null)
            invitation.setPersonFrom(peopleRepository.findById(invitation.getPersonFrom().getId()).get());
        if (invitation.getPersonTo() != null)
            invitation.setPersonTo(peopleRepository.findById(invitation.getPersonTo().getId()).get());

        int id = invitationsRepository.save(invitation).getId();
        invitation.setId(id);
    }

    @Transactional
    public void delete(int id) {
        invitationsRepository.deleteById(id);
    }

    @Transactional
    public void accept(int id) {
        Invitation invitation = invitationsRepository.findById(id).get();
        int personToTeamId = invitation.getPersonTo().getTeam().getId();

        // Приглашения, которые нужно удалить
        List<Invitation> invitationsToDelete = invitationsRepository.findByPersonToIdAndPersonFromTeamId(invitation.getPersonTo().getId(), invitation.getPersonFrom().getTeam().getId());
        List<Invitation> invitationsToDelete2 = invitationsRepository.findByPersonFromId(invitation.getPersonTo().getId());

        // Список людей, оставшихся в группе, которую покинул человек
        List<Person> peopleLeft = peopleRepository.findByTeamId(personToTeamId);

        // Перевод пользователя в другую группу
        if (invitation.getPersonTo().getRole().equals("ROLE_LEADER") && peopleLeft.size() != 1)
            throw new DataException("Person accepting the invitation should not be a leader!");
        invitation.getPersonTo().setTeam(invitation.getPersonFrom().getTeam());
        invitation.getPersonTo().setRole("ROLE_USER");

        if (peopleLeft.size() == 1)
            teamsRepository.deleteById(personToTeamId);

        for (Invitation invite : invitationsToDelete)
            invitationsRepository.deleteById(invite.getId());
        for (Invitation invite : invitationsToDelete2)
            invitationsRepository.deleteById(invite.getId());
    }
}
