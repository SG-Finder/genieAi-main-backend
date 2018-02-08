package com.finder.genie_ai.dao;

import com.finder.genie_ai.model.game.weapon.WeaponModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WeaponRepository extends JpaRepository<WeaponModel, Integer> {

    Optional<WeaponModel> findByName(String name);

}
