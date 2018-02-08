package com.finder.genie_ai.model.game.history;

import com.finder.genie_ai.model.game.player.PlayerModel;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "history")
@Data
public class HistoryModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(name = "win", nullable = false)
    private int win;
    @Column(name = "lose", nullable = false)
    private int lose;
    @Column(name = "one_shot", nullable = false)
    private int oneShot;
    @Column(name = "finder", nullable = false)
    private int finder;
    @Column(name = "last_week_rank", nullable = false)
    private int lastWeekRank;
    @OneToOne(targetEntity = PlayerModel.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "player_id", foreignKey = @ForeignKey(name = "FK_History_Players"), nullable = false)
    private PlayerModel playerId;

    @PrePersist
    public void persist() {
        this.win = 0;
        this.lose = 0;
        this.oneShot = 0;
        this.finder = 0;
        this.lastWeekRank = 0;
    }

}
