package com.example.demo.service;

import com.example.demo.model.Role;
import com.example.demo.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Optional<Role> getRoleById(Long id) {
        return roleRepository.findById(id);
    }

    public Optional<Role> getRoleByName(String name) {
        return Optional.ofNullable(roleRepository.findByRoleName(name));
    }

    @Transactional
    public Role createRole(Role role) {
        return roleRepository.save(role);
    }

    @Transactional
    public Role updateRole(Long id, Role roleDetails) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        
        role.setRoleName(roleDetails.getRoleName());
        role.setDescription(roleDetails.getDescription());
        return roleRepository.save(role);
    }

    @Transactional
    public void deleteRole(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new RuntimeException("Role not found");
        }
        roleRepository.deleteById(id);
    }
} 