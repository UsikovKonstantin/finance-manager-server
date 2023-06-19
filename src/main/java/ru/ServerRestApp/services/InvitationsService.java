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
    public Optional<Invitation> findById(int id) {
        return invitationsRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Invitation> findAll() {
        return invitationsRepository.findAll();
    }

    @Transactional
    public void save(Invitation invitation) {
        invitationsRepository.save(invitation);
    }

    @Transactional
    public void update(int id, Invitation updatedInvitation) {
        updatedInvitation.setId(id);
        invitationsRepository.save(updatedInvitation);
    }

    @Transactional
    public void delete(int id) {
        invitationsRepository.deleteById(id);
    }
}
