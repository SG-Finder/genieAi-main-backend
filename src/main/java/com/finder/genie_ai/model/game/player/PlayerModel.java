package com.finder.genie_ai.model.game.player;

import com.finder.genie_ai.enumdata.Tier;
import com.finder.genie_ai.model.user.UserModel;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "players")
@Data
public class PlayerModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(name = "nickname", length = 50, nullable = false, unique = true)
    private String nickname;
    @Enumerated(EnumType.STRING)
    @Column(name = "tier", nullable = false)
    private Tier tier;
    @Column(name = "score", nullable = false)
    private int score;
    @ManyToOne
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "FK_Players_Users"), nullable = false)
    private UserModel userId;
    @Column(name = "point", nullable = false)
    private int point;

    @PrePersist
    public void persist() {
        this.tier = Tier.BRONZE;
        this.score = 0;
        this.point = 0;
    }

}
