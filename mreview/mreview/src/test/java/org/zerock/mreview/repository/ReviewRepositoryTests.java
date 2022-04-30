package org.zerock.mreview.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zerock.mreview.entitiy.Member;
import org.zerock.mreview.entitiy.Movie;
import org.zerock.mreview.entitiy.Review;

import java.util.List;
import java.util.stream.IntStream;

@SpringBootTest
public class ReviewRepositoryTests {
    @Autowired
    private ReviewRepository reviewRepository;

    //@Test
    public void insertMovieReviews(){

        IntStream.rangeClosed(1,200).forEach(i->{

            Long mno = (long)(Math.random()*100)+1; // 영화 번호

            Long mid = ((long)(Math.random()*100)+1); // 리뷰어 번호

            Member member = Member.builder().mid(mid).build();

            Review movieReview = Review.builder()
                    .member(member)
                    .movie(Movie.builder().mno(mno).build())
                    .grade((int)(Math.random()*5)+1)
                    .text("이 영화에 대한 느낌 ... "+i)
                    .build();

            reviewRepository.save(movieReview);
        });
    }

    @Test
    public void testGetMovieReviews(){ // 특정 영화 리뷰 정보 확인 테스트

        Movie movie = Movie.builder().mno(98L).build();

        List<Review> result = reviewRepository.findByMovie(movie);

        result.forEach(movieReview ->{
            System.out.print(movieReview.getReviewnum());
            System.out.print("\t" + movieReview.getGrade());
            System.out.print("\t" + movieReview.getText());
            System.out.print("\t" + movieReview.getMember().getEmail());
            System.out.println("-------------------------------------");
        });

    }
}
