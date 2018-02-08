package com.finder.genie_ai.model.game.weapon;

import com.finder.genie_ai.model.game.BaseItemModel;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "weapons")
@Data
public class WeaponModel  extends BaseItemModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(name = "name", nullable = false)
    protected String name;
    @Column(name = "damage", nullable = false)
    protected int damage;
    @Column(name = "price", nullable = false)
    protected int price;

}
