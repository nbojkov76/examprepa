package softuni.exam.instagraphlite.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.instagraphlite.models.Dto.UserSeedDto;
import softuni.exam.instagraphlite.models.entity.User;
import softuni.exam.instagraphlite.repository.UserRepository;
import softuni.exam.instagraphlite.service.PictureService;
import softuni.exam.instagraphlite.service.UserService;
import softuni.exam.instagraphlite.util.ValidationUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    private final Gson gson;

    public static final String USER_PATH = "src/main/resources/files/users.json";
    private final ValidationUtil validationUtil;
    private final PictureService pictureService;

    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper, Gson gson, ValidationUtil validationUtil, PictureService pictureService) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.gson = gson;
        this.validationUtil = validationUtil;
        this.pictureService = pictureService;
    }

    @Override
    public boolean areImported() {
        return userRepository.count() > 0;
    }

    @Override
    public String readFromFileContent() throws IOException {
        return Files.readString(Path.of(USER_PATH));
    }

    @Override
    public String importUsers() throws IOException {

        StringBuilder sb = new StringBuilder();
         Arrays.stream(gson.fromJson(readFromFileContent(), UserSeedDto[].class))
                 .filter(userSeedDto -> {
                  boolean isValid = validationUtil.isValid(userSeedDto)
                          && !isEntityExsits(userSeedDto.getUsername())
                          && pictureService.isEntityExists(userSeedDto.getProfilePicture());

                            sb.append(isValid ? "Successfully imported User: " + userSeedDto.getUsername()
                                    : "Invalid User").append(System.lineSeparator());
                        return isValid;
                 }).map(userSeedDto -> {
                     User user = modelMapper.map(userSeedDto, User.class);
                     user.setProfilePicture(pictureService
                             .findByPath(userSeedDto.getProfilePicture()));
                        return user;

                 }).forEach(userRepository::save);


        return sb.toString();
    }
@Override
public boolean isEntityExsits(String username) {
return userRepository.existsByUsername(username);
    }

    @Override
    public String exportUsersWithTheirPosts() {

        StringBuilder sb = new StringBuilder();

        userRepository.findAllByPostsCountDescThanByUserId().forEach(user -> {
            sb.append(String.format("User: %s\n" +
                    "Post count: %d\n", user.getUsername(), user.getPosts().size()));

            user.getPosts().forEach(post -> {
                sb.append(String.format("==Post Details:\n" +
                        "----Caption: %s\n" +
                        "----Picture Size: %.2f\n", post.getCaption(), post.getPicture().getSize()));
            });
        });
        return sb.toString();
    }

    @Override
    public User finfByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
}
