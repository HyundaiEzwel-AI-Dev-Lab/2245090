package com.ezwel.pms.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<PmsUser, Long> {
}
