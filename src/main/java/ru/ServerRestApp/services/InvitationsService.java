package ru.ServerRestApp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ServerRestApp.models.Invitation;
import ru.ServerRestApp.models.Person;
import ru.ServerRestApp.models.Team;
import ru.ServerRestApp.repositories.InvitationsRepository;
import ru.ServerRestApp.repositories.PeopleRepository;
import ru.ServerRestApp.repositories.TeamsRepository;

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

        // Перевод пользователя в другую группу
        invitation.getPersonTo().setTeam(invitation.getPersonFrom().getTeam());
        invitation.getPersonTo().setRole("ROLE_USER");

        // Список людей, оставшихся в группе, которую покинул человек
        List<Person> peopleLeft = peopleRepository.findByTeamId(personToTeamId);

        // Список админов в этой группе
        List<Person> peopleLeftLeaders = peopleRepository.findByTeamIdAndRole(personToTeamId, "ROLE_LEADER");

        if (peopleLeft.size() == 0)
            teamsRepository.deleteById(personToTeamId);
        else if (peopleLeftLeaders.size() == 0)
            peopleLeft.get(0).setRole("ROLE_LEADER");

        for (Invitation invite : invitationsToDelete)
            invitationsRepository.deleteById(invite.getId());
        for (Invitation invite : invitationsToDelete2)
            invitationsRepository.deleteById(invite.getId());
    }
}
