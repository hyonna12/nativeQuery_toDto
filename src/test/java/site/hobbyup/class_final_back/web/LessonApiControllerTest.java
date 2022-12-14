package site.hobbyup.class_final_back.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.Timestamp;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;

import site.hobbyup.class_final_back.config.dummy.DummyEntity;
import site.hobbyup.class_final_back.config.enums.DayEnum;
import site.hobbyup.class_final_back.domain.category.Category;
import site.hobbyup.class_final_back.domain.category.CategoryRepository;
import site.hobbyup.class_final_back.domain.lesson.Lesson;
import site.hobbyup.class_final_back.domain.lesson.LessonRepository;
import site.hobbyup.class_final_back.domain.profile.Profile;
import site.hobbyup.class_final_back.domain.profile.ProfileRepository;
import site.hobbyup.class_final_back.domain.review.Review;
import site.hobbyup.class_final_back.domain.review.ReviewRepository;
import site.hobbyup.class_final_back.domain.user.User;
import site.hobbyup.class_final_back.domain.user.UserRepository;
import site.hobbyup.class_final_back.dto.lesson.LessonReqDto.LessonSaveReqDto;
import site.hobbyup.class_final_back.util.DecodeUtil;

@Sql("classpath:db/truncate.sql") // ?????? ?????? ?????? (auto_increment ????????? + ????????? ?????????)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
public class LessonApiControllerTest extends DummyEntity {

  private static final String APPLICATION_JSON_UTF8 = "application/json; charset=utf-8";

  @Autowired
  private MockMvc mvc;

  @Autowired
  private ObjectMapper om;

  @Autowired
  private LessonRepository lessonRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private ReviewRepository reviewRepository;

  @Autowired
  private ProfileRepository profileRepository;

  @Autowired
  private EntityManager em;

  @BeforeEach
  public void setUp() {
    User ssar = userRepository.save(newUser("ssar"));
    User cos = userRepository.save(newUser("cos"));

    Category beauty = categoryRepository.save(newCategory("??????"));
    Category sports = categoryRepository.save(newCategory("?????????"));
    Category dance = categoryRepository.save(newCategory("??????"));
    Category music = categoryRepository.save(newCategory("??????"));
    Category art = categoryRepository.save(newCategory("??????"));
    Category crafts = categoryRepository.save(newCategory("??????"));
    Category game = categoryRepository.save(newCategory("??????"));
    Category others = categoryRepository.save(newCategory("??????"));

    Profile ssarProfile = profileRepository
        .save(newProfile("", "??????????????? ???????????? ?????? ????????? ?????? ssar?????????.", "??????", "?????????", "5???", "?????? ????????? ????????? ?????? 10???", ssar));

    Lesson lesson1 = lessonRepository.save(newLesson("??????1", 10000L, ssar, beauty));
    Lesson lesson2 = lessonRepository.save(newLesson("??????2", 20000L, ssar, sports));
    Lesson lesson3 = lessonRepository.save(newLesson("??????3", 50000L, ssar, music));
    Lesson lesson4 = lessonRepository.save(newLesson("??????4", 34500L, cos, music));
    Lesson lesson5 = lessonRepository.save(newLesson("??????5", 2400L, cos, music));
    Lesson lesson6 = lessonRepository.save(newLesson("??????6", 98000000L, cos, beauty));
    Lesson lesson7 = lessonRepository.save(newLesson("??????7", 30000L, ssar, sports));
    Lesson lesson8 = lessonRepository.save(newLesson("??????8", 40000L, ssar, sports));
    Lesson lesson9 = lessonRepository.save(newLesson("??????9", 50000L, ssar, sports));
    Lesson lesson10 = lessonRepository.save(newLesson("??????10", 70000L, ssar, sports));

    Review review1 = reviewRepository.save(newReivew("?????? ?????? ???????????????.", 4.5, ssar, lesson1));
    Review review2 = reviewRepository.save(newReivew("???????????? ????????? ??? ?????????!", 4.0, cos, lesson1));
    Review review3 = reviewRepository.save(newReivew("????????????", 3.0, ssar, lesson2));
    Review review4 = reviewRepository.save(newReivew("????????? ??? ?????? ????????? ????????? ???????", 2.5, ssar, lesson2));

  }

  @WithUserDetails(value = "ssar", setupBefore = TestExecutionEvent.TEST_EXECUTION)
  @Test
  public void saveLesson_test() throws Exception {
    // given
    LessonSaveReqDto lessonSaveReqDto = new LessonSaveReqDto();
    String realPhoto = "";
    String photo = DecodeUtil.saveDecodingImage(realPhoto);

    lessonSaveReqDto.setName("?????????????????? ???????????? ???????????? ?????? ???????????? ???");
    lessonSaveReqDto.setCategoryId(4L);
    lessonSaveReqDto.setCurriculum("1??? : ????????? ??????, 2??? : ?????? ????????? ??????, 3??? : ?????? ??? ?????????");
    lessonSaveReqDto.setPhoto(photo);
    lessonSaveReqDto.setPlace("????????????");
    lessonSaveReqDto.setExpiredAt(new Timestamp(700000000L));
    lessonSaveReqDto.setPolicy("?????? ??? ????????????");
    lessonSaveReqDto.setPossibleDays(DayEnum.MONDAY);
    lessonSaveReqDto.setPrice(500000L);

    String requestBody = om.writeValueAsString(lessonSaveReqDto);

    // when
    ResultActions resultActions = mvc
        .perform(post("/api/lesson").content(requestBody)
            .contentType(APPLICATION_JSON_UTF8));
    String responseBody = resultActions.andReturn().getResponse().getContentAsString();
    System.out.println("????????? : " + responseBody);

    // then
    resultActions.andExpect(status().isCreated());
    resultActions.andExpect(jsonPath("$.data.name").value("?????????????????? ???????????? ???????????? ?????? ???????????? ???"));
    resultActions.andExpect(jsonPath("$.data.category.name").value("??????"));
    resultActions.andExpect(jsonPath("$.data.user.id").value(1L));
    resultActions.andExpect(jsonPath("$.data.id").value(11L));
  }

  @Test
  public void getLessonCategoryList_test() throws Exception {
    // given
    Long categoryId = 2L;
    Long minPrice = 5000L;
    Long maxPrice = 50000L;

    // when
    ResultActions resultActions = mvc
        .perform(get("/api/category/" + categoryId + "?min_price=" + minPrice + "&max_price=" + maxPrice));
    String responseBody = resultActions.andReturn().getResponse().getContentAsString();
    System.out.println("????????? : " + responseBody);

    // then
    resultActions.andExpect(status().isOk());
    resultActions.andExpect(jsonPath("$.data.categoryDto.categoryName").value("?????????"));
    resultActions.andExpect(jsonPath("$.data.lessonDtoList[0].lessonPrice").value("20000???"));
  }

  @Test
  public void getLessonDetail_test() throws Exception {
    // given
    Long lessonId = 1L;

    // when
    ResultActions resultActions = mvc
        .perform(get("/api/lesson/" + lessonId));
    String responseBody = resultActions.andReturn().getResponse().getContentAsString();
    System.out.println("????????? : " + responseBody);

    // then
    resultActions.andExpect(status().isOk());
    resultActions.andExpect(jsonPath("$.data.lessonName").value("??????1"));
    resultActions.andExpect(jsonPath("$.data.lessonReviewList[0].reviewContent").value("?????? ?????? ???????????????."));
  }

}
