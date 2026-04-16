package web.sportflow.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import org.springframework.core.io.ClassPathResource;
import web.sportflow.badge.Badge;
import web.sportflow.badge.BadgeRepository;
import web.sportflow.user.User;
import web.sportflow.user.UserRepository;

final class ReferenceDemoAssets {

  private ReferenceDemoAssets() {}

  static void assignAvatars(
      Map<User, String> avatarByUser, String avatarUploadDir, UserRepository userRepository) {
    try {
      Path uploadDir = Paths.get(avatarUploadDir).toAbsolutePath().normalize();
      Files.createDirectories(uploadDir);
      for (Map.Entry<User, String> entry : avatarByUser.entrySet()) {
        User user = entry.getKey();
        String sourceFileName = entry.getValue();
        if (user == null
            || user.getId() == null
            || sourceFileName == null
            || sourceFileName.isBlank()) {
          continue;
        }

        cleanupExistingUserAvatars(uploadDir, user.getId());

        String extension = extractExtension(sourceFileName);
        if (extension.isBlank()) {
          continue;
        }

        String targetFileName = "user_" + user.getId() + "." + extension;
        Path targetPath = uploadDir.resolve(targetFileName).normalize();
        if (!targetPath.startsWith(uploadDir)) {
          continue;
        }

        ClassPathResource resource =
            new ClassPathResource("static/images/avatars/" + sourceFileName);
        if (!resource.exists()) {
          continue;
        }

        try (InputStream inputStream = resource.getInputStream()) {
          Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
          user.setProfileImagePath("/avatar_upload/" + targetFileName);
          userRepository.save(user);
        }
      }
    } catch (IOException exception) {
      throw new IllegalStateException("Impossible de copier les avatars de demo.", exception);
    }
  }

  static void assignBadgeIcons(
      List<Badge> badges, String badgeUploadDir, BadgeRepository badgeRepository) {
    try {
      Path uploadDir = Paths.get(badgeUploadDir).toAbsolutePath().normalize();
      Files.createDirectories(uploadDir);
      for (Badge badge : badges) {
        if (badge == null) {
          continue;
        }
        String sourceFileName = extractFileName(badge.getIconPath());
        if (sourceFileName.isBlank()) {
          continue;
        }
        String extension = extractExtension(sourceFileName);
        if (extension.isBlank()) {
          continue;
        }

        Path targetPath = uploadDir.resolve(sourceFileName).normalize();
        if (!targetPath.startsWith(uploadDir)) {
          continue;
        }

        ClassPathResource resource = new ClassPathResource("static/images/badge/" + sourceFileName);
        if (!resource.exists()) {
          continue;
        }

        try (InputStream inputStream = resource.getInputStream()) {
          Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
          badge.setIconPath("/badge_upload/" + sourceFileName);
        }
      }
      badgeRepository.saveAll(badges);
    } catch (IOException exception) {
      throw new IllegalStateException("Impossible de copier les badges de demo.", exception);
    }
  }

  private static String extractFileName(String pathValue) {
    if (pathValue == null || pathValue.isBlank()) {
      return "";
    }
    String normalizedPath = pathValue.trim().replace('\\', '/');
    int queryIndex = normalizedPath.indexOf('?');
    if (queryIndex >= 0) {
      normalizedPath = normalizedPath.substring(0, queryIndex);
    }
    int fragmentIndex = normalizedPath.indexOf('#');
    if (fragmentIndex >= 0) {
      normalizedPath = normalizedPath.substring(0, fragmentIndex);
    }
    int lastSlashIndex = normalizedPath.lastIndexOf('/');
    String fileName =
        lastSlashIndex >= 0 ? normalizedPath.substring(lastSlashIndex + 1) : normalizedPath;
    if (fileName.isBlank() || fileName.contains("..")) {
      return "";
    }
    return fileName;
  }

  private static String extractExtension(String filename) {
    if (filename == null || !filename.contains(".")) {
      return "";
    }
    int lastDotIndex = filename.lastIndexOf('.');
    if (lastDotIndex == filename.length() - 1) {
      return "";
    }
    return filename.substring(lastDotIndex + 1);
  }

  private static void cleanupExistingUserAvatars(Path uploadDir, Long userId) throws IOException {
    String pattern = "user_" + userId + ".*";
    try (DirectoryStream<Path> stream = Files.newDirectoryStream(uploadDir, pattern)) {
      for (Path path : stream) {
        Files.deleteIfExists(path);
      }
    }
  }
}
