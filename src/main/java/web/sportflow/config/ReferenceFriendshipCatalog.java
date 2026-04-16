package web.sportflow.config;

import java.util.List;
import java.util.Map;
import web.sportflow.friendship.FriendshipService;
import web.sportflow.user.User;

final class ReferenceFriendshipCatalog {

  private static final List<FriendshipDto> FRIENDSHIPS =
      List.of(
          accepted("alice", "owen"),
          requested("benoit", "alice"),
          requested("admin", "judy"),
          accepted("judy", "nick"),
          accepted("hiccup", "astrid"),
          accepted("stoick", "fishlegs"),
          accepted("rodney", "cappy"),
          accepted("fender", "bigweld"),
          accepted("judy", "astrid"),
          accepted("nick", "rodney"),
          accepted("bogo", "stoick"),
          requested("bellwether", "judy"),
          requested("cappy", "astrid"),
          requested("hiccup", "rodney"),
          requested("fender", "nick"),
          requested("bigweld", "bogo"),
          accepted("shifu", "po"),
          accepted("oogway", "shifu"),
          requested("taiLung", "shifu"));

  private ReferenceFriendshipCatalog() {}

  static void seed(FriendshipService friendshipService, Map<String, User> usersByKey) {
    for (FriendshipDto friendship : FRIENDSHIPS) {
      User source = requireUser(usersByKey, friendship.sourceUserKey());
      User target = requireUser(usersByKey, friendship.targetUserKey());
      if (friendship.accepted()) {
        friendshipService.createAcceptedFriendship(source.getId(), target.getId());
      } else {
        friendshipService.sendRequest(source.getId(), target.getId());
      }
    }
  }

  private static FriendshipDto accepted(String sourceUserKey, String targetUserKey) {
    return new FriendshipDto(sourceUserKey, targetUserKey, true);
  }

  private static FriendshipDto requested(String sourceUserKey, String targetUserKey) {
    return new FriendshipDto(sourceUserKey, targetUserKey, false);
  }

  private static User requireUser(Map<String, User> usersByKey, String userKey) {
    User user = usersByKey.get(userKey);
    if (user == null) {
      throw new IllegalStateException("Utilisateur de demo introuvable: " + userKey);
    }
    return user;
  }

  private record FriendshipDto(String sourceUserKey, String targetUserKey, boolean accepted) {}
}
