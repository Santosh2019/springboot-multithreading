package com.multithreading.springboot_multithreading.service;

import com.multithreading.springboot_multithreading.entity.UserEntity;
import com.multithreading.springboot_multithreading.repo.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class UserService {
    @Autowired
    private UserRepo userRepo;
    Logger logger = LoggerFactory.getLogger(UserService.class);


    @Async
    public CompletableFuture<List<UserEntity>> saveUser(MultipartFile file) throws Exception {
        long start = System.currentTimeMillis();
        List<UserEntity> userEntities = parCsv(file);
        logger.info("saving list of users size {}", userEntities.size(), "" + Thread.currentThread().getName());
        userEntities = userRepo.saveAll(userEntities);
        long end = System.currentTimeMillis();
        logger.info("Total time:{}", end - start);

        return CompletableFuture.completedFuture(userEntities);
    }

    @Async
    public CompletableFuture<List<UserEntity>> findAll() {
        logger.info("get list of user by ", Thread.currentThread().getName());
        List<UserEntity> list = userRepo.findAll();
        return CompletableFuture.completedFuture(list);
    }


    private List<UserEntity> parCsv(final MultipartFile file) throws Exception {
        final List<UserEntity> users = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                final String[] data = line.split(",");

                final UserEntity user = new UserEntity();
                user.setEmail(data[0]);
                user.setName(data[1]);
                user.setGender(data[2]);

                users.add(user);
            }
            return users;
        } catch (final Exception e) {
            logger.error("failed to parse csv file {}", e);
            throw new Exception("Failed to parse csv file");
        }
    }
}