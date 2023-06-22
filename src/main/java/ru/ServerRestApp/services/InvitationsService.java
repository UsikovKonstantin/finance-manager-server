package ru.ServerRestApp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ServerRestApp.models.Invitation;
import ru.ServerRestApp.models.Person;
import ru.ServerRestApp.repositories.InvitationsRepository;
import ru.ServerRestApp.repositories.PeopleRepository;

import java.util.List;
import java.util.Optional;

@Service
public class InvitationsService {

    private final InvitationsRepository invitationsRepository;
    private final PeopleRepository peopleRepository;
    @Autowired
    public InvitationsService(InvitationsRepository invitationsRepository, PeopleRepository peopleRepository) {
        this.invitationsRepository = invitationsRepository;
        this.peopleRepository = peopleRepository;
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
}
