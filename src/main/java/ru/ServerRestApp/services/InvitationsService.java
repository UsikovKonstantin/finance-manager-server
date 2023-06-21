package ru.ServerRestApp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ServerRestApp.models.Invitation;
import ru.ServerRestApp.models.Team;
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
    public Invitation findById(int id) {
        Optional<Invitation> invitation = invitationsRepository.findById(id);
        return invitation.get();
    }

    @Transactional(readOnly = true)
    public List<Invitation> findAll() {
        return invitationsRepository.findAll();
    }

    @Transactional
    public void save(Invitation invitation) {
        if (invitation.getPerson_from() != null)
            invitation.setPerson_from(peopleRepository.findById(invitation.getPerson_from().getId()).get());
        if (invitation.getPerson_to() != null)
            invitation.setPerson_to(peopleRepository.findById(invitation.getPerson_to().getId()).get());
        invitationsRepository.save(invitation);
    }

    @Transactional
    public void update(Invitation invitation) {
        if (invitation.getPerson_from() != null)
            invitation.setPerson_from(peopleRepository.findById(invitation.getPerson_from().getId()).get());
        if (invitation.getPerson_to() != null)
            invitation.setPerson_to(peopleRepository.findById(invitation.getPerson_to().getId()).get());
        invitationsRepository.save(invitation);
    }

    @Transactional
    public void delete(int id) {
        invitationsRepository.deleteById(id);
    }
}
