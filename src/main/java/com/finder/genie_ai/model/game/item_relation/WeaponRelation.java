package com.finder.genie_ai.model.game.item_relation;

import com.finder.genie_ai.model.game.player.PlayerModel;
import com.finder.genie_ai.model.game.weapon.WeaponModel;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "weapon_relation")
@Data
public class WeaponRelation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected int id;

    @ManyToOne
    @JoinColumn(name = "player_id",foreignKey = @ForeignKey(name = "FK_Items_Players"), nullable = false)
    private PlayerModel playerId;

    @ManyToOne
    @JoinColumn(name = "weapon_id",foreignKey = @ForeignKey(name = "FK_Items_Weapons"), nullable = false)
    private WeaponModel weaponId;

    @Column(name = "usable_count", nullable = false)
    protected int usableCount;

}
