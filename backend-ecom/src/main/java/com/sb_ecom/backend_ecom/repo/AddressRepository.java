package com.sb_ecom.backend_ecom.repo;


import com.sb_ecom.backend_ecom.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
