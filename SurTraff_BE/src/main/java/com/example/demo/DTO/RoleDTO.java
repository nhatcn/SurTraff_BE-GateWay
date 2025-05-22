package com.example.demo.DTO;

public class RoleDTO {
    private Long id;
    private String roleName;

    public RoleDTO(Long id, String roleName) {
        this.id = id;
        this.roleName = roleName;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.roleName;
    }

    public void setName(String roleName) {
        this.roleName = roleName;
    }


}
