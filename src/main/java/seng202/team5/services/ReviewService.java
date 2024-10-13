package seng202.team5.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team5.exceptions.DuplicateEntryException;
import seng202.team5.models.Review;
import seng202.team5.repository.ReviewDAO;

/**
 * Review Service to handle Review related logic.
 *
 * @author Sean Reitsma
 */
public class ReviewService {
    private static final Logger log = LogManager.getLogger(ReviewService.class);
    private final ReviewDAO reviewDAO;
    private final UserService userService;

    /**
     * Basic constructor for the ReviewService class.
     */
    public ReviewService() {
        reviewDAO = new ReviewDAO();
        userService = UserService.getInstance();
    }

    /**
     * Handles creating a new review if the review doesn't exist - called if a user edits anything
     * and a review doesn't already exist.
     *
     * @param review the Review object, null if it doesn't exist
     * @param selectedWineId the Wine id for the Review
     * @param userId the User id for the Review
     * @return the review that was created
     */
    public Review createReviewIfNotExists(Review review, int selectedWineId, int userId) {
        Review result = review;
        if (review == null && userService.getCurrentUser() != null) {
            result = new Review(selectedWineId, userId);
            try {
                reviewDAO.add(result);
            } catch (DuplicateEntryException e) {
                log.error(e);
            }
        }
        return result;
    }
}
