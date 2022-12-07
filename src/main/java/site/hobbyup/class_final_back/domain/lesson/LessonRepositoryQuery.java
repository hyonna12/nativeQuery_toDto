package site.hobbyup.class_final_back.domain.lesson;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.qlrm.mapper.JpaResultMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import site.hobbyup.class_final_back.dto.lesson.LessonRespDto.LessonLatestRespDto;

@Repository
public class LessonRepositoryQuery {

    @Autowired
    private EntityManager em;

    public LessonLatestRespDto findAllLatest() {
        StringBuffer sb = new StringBuffer();
        // sb.append("SELECT l.name, l.price, l.photo, avg(r.grade), count(r.lessonId)
        // FROM Lesson l " +
        // "LEFT JOIN Review r ON r.lessonId = l.id " +
        // "GROUP BY r.lessonId " +
        // "ORDER BY l.createdAt desc LIMIT 12");

        sb.append(
                "SELECT l.name name, l.price price, l.photo photo, avg(r.grade) avgGrade, count(r.lesson_id) totalReview FROM lesson l LEFT JOIN review r ON r.lesson_id = l.id GROUP BY r.lesson_id ORDER BY created_at desc LIMIT 12");

        // 쿼리 완성
        Query query = em.createQuery(sb.toString());
        // 쿼리 실행(qlrm 라이브러리 필요 - dto에 db 결과를 매핑하기 위해서)
        JpaResultMapper result = new JpaResultMapper();
        LessonLatestRespDto lessonLatestRespDto = result.uniqueResult(query, LessonLatestRespDto.class);
        return lessonLatestRespDto;
    }
}
