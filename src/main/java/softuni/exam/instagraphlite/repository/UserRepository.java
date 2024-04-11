package softuni.exam.instagraphlite.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import softuni.exam.instagraphlite.models.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User , Long> {
    boolean  existsByUsername(String username);

    Optional<User> findByUsername(String username);
@Query("SELECT distinct u from User u JOIN  FETCH u.posts p order by size(p) DESC, u.id")
    List<User> findAllByPostsCountDescThanByUserId() ;
}
