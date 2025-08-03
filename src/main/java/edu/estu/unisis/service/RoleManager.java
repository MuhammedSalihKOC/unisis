package edu.estu.unisis.service;

import edu.estu.unisis.model.Role;
import edu.estu.unisis.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleManager implements RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleManager(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role getRoleByName(String name) {
        return roleRepository.findByName(name);
    }
}
