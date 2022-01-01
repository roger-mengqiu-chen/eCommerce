package com.example.eComPractice.model.persistence.repositories;


import com.example.eComPractice.model.persistence.Cart;
import com.example.eComPractice.model.persistence.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
	Cart findByUser(User user);
}
