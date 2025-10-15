package com.i2i.usermanagement.task;

import com.i2i.usermanagement.dto.UserCreateDTO;
import com.i2i.usermanagement.dto.UserResponseDTO;
import com.i2i.usermanagement.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

/**
 * Fork/Join task for bulk user creation.
 * Splits user creation work into smaller chunks for parallel processing.
 *
 * @author Agnel Ruban
 * @version 1.0
 * @since 13-10-2025
 */
public class BulkUserCreationTask extends RecursiveTask<List<UserResponseDTO>> {

    private static final Logger logger = LoggerFactory.getLogger(BulkUserCreationTask.class);
    private static final int THRESHOLD = 5;   //Process 5 users at a time
    private final List<UserCreateDTO> userDTOs;
    private final int start;
    private final int end;
    private final UserService userService;

    public BulkUserCreationTask(List<UserCreateDTO> userDTOs, int start, int end, UserService userService) {
        this.userDTOs = userDTOs;
        this.start = start;
        this.end = end;
        this.userService = userService;
    }

    @Override
    protected List<UserResponseDTO> compute() {
        // Base case: if chunk is small enough, process directly
        if (end - start <= THRESHOLD) {
            List<UserResponseDTO> results = new ArrayList<>();
            for (int i = start; i < end; i++) {
                try {
                    UserResponseDTO user = userService.createUser(userDTOs.get(i));
                    results.add(user);
                } catch (Exception exception) {
                    // Log error but continue with other users
                    logger.error("Error creating user at index {}: {}", i, exception.getMessage());
                }
            }
            return results;
        }

        // Divide: split the work into two halves

        return getUserResponseDTOS();
    }

    private List<UserResponseDTO> getUserResponseDTOS() {
        int mid = (start + end) / 2;
        BulkUserCreationTask leftTask = new BulkUserCreationTask(userDTOs, start, mid, userService);
        BulkUserCreationTask rightTask = new BulkUserCreationTask(userDTOs, mid, end, userService);

        // Fork: start the left task asynchronously
        leftTask.fork();

        // Compute: process the right task
        List<UserResponseDTO> rightResults = rightTask.compute();

        // Join: wait for the left task to complete
        List<UserResponseDTO> leftResults = leftTask.join();

        // Combine results
        List<UserResponseDTO> allResults = new ArrayList<>();
        allResults.addAll(leftResults);
        allResults.addAll(rightResults);
        return allResults;
    }
}
