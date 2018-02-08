package com.finder.genie_ai.dao;

import com.finder.genie_ai.model.user.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserModel, Integer> {

    Optional<UserModel> findByUserId(String userId);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "UPDATE users" +
            "SET user_name = :userName, passwd :passwd, email = :email, birth = :birth, introduce = :introduce " +
            "WHERE user_id = :userId", nativeQuery = true)
    int updateUserInfo(@Param("userId") String userId,
                       @Param("passwd") String passwd,
                       @Param("userName") String userName,
                       @Param("email") String email,
                       @Param("birth") LocalDate birth,
                       @Param("introduce") String introduce);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM users WHERE user_id = :userId", nativeQuery = true)
    void deleteByUserId(@Param("userId") String userId);

}
