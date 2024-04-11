package softuni.exam.instagraphlite.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.instagraphlite.models.Dto.PostSeedRootDto;
import softuni.exam.instagraphlite.models.entity.Posts;
import softuni.exam.instagraphlite.repository.PostRepository;
import softuni.exam.instagraphlite.service.PictureService;
import softuni.exam.instagraphlite.service.PostService;
import softuni.exam.instagraphlite.service.UserService;
import softuni.exam.instagraphlite.util.ValidationUtil;
import softuni.exam.instagraphlite.util.XmlParser;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class PostServiceImpl implements PostService {

    private  final PostRepository postRepository;

    private final UserService userService;
    private final PictureService pictureService;

   private final XmlParser xmlParser;

    public static final String POST_FILE = "src/main/resources/files/posts.xml";

    private final ModelMapper modelMapper;

    private final ValidationUtil validationUtil;

    public PostServiceImpl(PostRepository postRepository, UserService userService, PictureService pictureService, XmlParser xmlParser, ModelMapper modelMapper, ValidationUtil validationUtil) {
        this.postRepository = postRepository;
        this.userService = userService;
        this.pictureService = pictureService;
        this.xmlParser = xmlParser;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
    }

    @Override
    public boolean areImported() {
        return postRepository.count() > 0;
    }

    @Override
    public String readFromFileContent() throws IOException {
        return Files.readString(Path.of(POST_FILE));
    }

    @Override
    public String importPosts() throws IOException, JAXBException {

        StringBuilder sb = new StringBuilder();

        xmlParser.fromFile(POST_FILE, PostSeedRootDto.class)
                .getPosts()
                .stream()
                .filter(postSeedDto -> {


                    boolean isValid = validationUtil.isValid(postSeedDto)
                            && userService.isEntityExsits(postSeedDto.getUsername().getUsername())
                            && pictureService.isEntityExists(postSeedDto.getPicture().getPath());

                    sb.append(isValid ? "Successfully imported Post, made by " + postSeedDto.getUsername().getUsername()
                            : "Invalid Post").append(System.lineSeparator());


                        return isValid;


                }).map(postSeedDto -> {
                    Posts posts = modelMapper.map(postSeedDto, Posts.class);
                    posts.setUser(userService.finfByUsername(postSeedDto.getUsername().getUsername()));
                    posts.setPicture(pictureService.findByPath(postSeedDto.getPicture().getPath()));
                        return posts;
                }).forEach(postRepository::save);


        return sb.toString();
    }
}
