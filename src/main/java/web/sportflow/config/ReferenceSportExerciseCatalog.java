package web.sportflow.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import web.sportflow.exercise.Exercise;
import web.sportflow.exercise.ExerciseRepository;
import web.sportflow.sport.Sport;
import web.sportflow.sport.SportRepository;

final class ReferenceSportExerciseCatalog {

  private static final List<SportDto> SPORTS =
      List.of(
          new SportDto("Escalade", 8.0),
          new SportDto("Alpinisme", 7.5),
          new SportDto("Randonnee", 6.0),
          new SportDto("Course", 9.8),
          new SportDto("Lance", 4.5),
          new SportDto("Marathon", 10.5),
          new SportDto("Marche", 4.3),
          new SportDto("Saut", 6.5),
          new SportDto("Cyclisme", 8.0),
          new SportDto("Musculation", 6.0),
          new SportDto("Callisthenie", 5.5),
          new SportDto("CrossFit", 8.0),
          new SportDto("Natation", 8.3),
          new SportDto("Plongee", 7.0),
          new SportDto("Saut_Parachute", 3.5),
          new SportDto("Base_Jump", 4.0),
          new SportDto("Tennis", 7.3),
          new SportDto("Ping_Pong", 4.0),
          new SportDto("Squash", 7.3),
          new SportDto("Football", 7.0),
          new SportDto("Basketball", 8.0),
          new SportDto("Judo", 10.3),
          new SportDto("Taekwondo", 10.3),
          new SportDto("Karate", 10.0),
          new SportDto("Boxe", 12.8),
          new SportDto("Escrime", 6.0),
          new SportDto("Lutte", 10.3),
          new SportDto("Ski", 7.0),
          new SportDto("Curling", 4.0),
          new SportDto("Hockey", 8.0),
          new SportDto("Luge", 4.0),
          new SportDto("Patinage", 7.0),
          new SportDto("Bobsleigh", 5.0),
          new SportDto("Parkour", 8.5),
          new SportDto("Skate", 5.0),
          new SportDto("Tir_Sportif", 2.5),
          new SportDto("Tir_Arc", 3.5),
          new SportDto("Tir_Cible", 2.5),
          new SportDto("Repassage_Extrem", 4.0),
          new SportDto("Gymnastique", 5.5),
          new SportDto("Yoga", 3.3),
          new SportDto("Pentathlon", 8.0),
          new SportDto("Triathlon", 9.5),
          new SportDto("Formule_1", 4.0),
          new SportDto("Motocyclisme", 4.0),
          new SportDto("Aviron", 7.0),
          new SportDto("Canoe_Kayak", 6.0),
          new SportDto("Surf", 3.0),
          new SportDto("Voile", 3.0),
          new SportDto("Equitation", 5.5),
          new SportDto("Speleologie", 6.0));

  private static final List<ExerciseDto> EXERCISES =
      List.of(
          new ExerciseDto("Course continue", 0.15),
          new ExerciseDto("Sprint", 0.22),
          new ExerciseDto("Montee de cote", 0.18),
          new ExerciseDto("Circuit cardio", 0.16),
          new ExerciseDto("Burpees", 0.18),
          new ExerciseDto("Pompes", 0.10),
          new ExerciseDto("Squat", 0.11),
          new ExerciseDto("Developpe couche", 0.12),
          new ExerciseDto("Developpe militaire", 0.10),
          new ExerciseDto("Tractions", 0.13),
          new ExerciseDto("Nage libre", 0.13),
          new ExerciseDto("Crawl", 0.14),
          new ExerciseDto("Escalade bloc", 0.14),
          new ExerciseDto("Voie de vitesse", 0.16),
          new ExerciseDto("Pedalage endurance", 0.14),
          new ExerciseDto("Dribble et tirs", 0.12),
          new ExerciseDto("Yoga flow", 0.06),
          new ExerciseDto("Marche sur sentier", 0.09),
          new ExerciseDto("Saut technique", 0.13),
          new ExerciseDto("Lancer medecine ball", 0.09),
          new ExerciseDto("Shadow boxing", 0.15),
          new ExerciseDto("Travail des appuis", 0.11),
          new ExerciseDto("Kata technique", 0.10),
          new ExerciseDto("Randori", 0.17),
          new ExerciseDto("Glisse endurance", 0.12),
          new ExerciseDto("Tir de precision", 0.04),
          new ExerciseDto("Gainage", 0.07),
          new ExerciseDto("Rame endurance", 0.13),
          new ExerciseDto("Pagaie endurance", 0.11),
          new ExerciseDto("Equilibre", 0.05),
          new ExerciseDto("Pilotage technique", 0.07),
          new ExerciseDto("Approche alpine", 0.12),
          new ExerciseDto("Progression encordee", 0.11),
          new ExerciseDto("Randonnee avec denivele", 0.10),
          new ExerciseDto("Marche nordique", 0.08),
          new ExerciseDto("Lancer de javelot", 0.09),
          new ExerciseDto("Lancer de poids", 0.10),
          new ExerciseDto("Saut en hauteur", 0.12),
          new ExerciseDto("Saut en longueur", 0.12),
          new ExerciseDto("Sortie avion", 0.05),
          new ExerciseDto("Atterrissage de precision", 0.06),
          new ExerciseDto("Exit falaise", 0.06),
          new ExerciseDto("Pilotage voile base jump", 0.07),
          new ExerciseDto("Service tennis", 0.10),
          new ExerciseDto("Echanges fond de court", 0.12),
          new ExerciseDto("Service et remise", 0.06),
          new ExerciseDto("Echanges coup droit revers", 0.07),
          new ExerciseDto("Rally squash", 0.13),
          new ExerciseDto("Deplacements lateraux squash", 0.12),
          new ExerciseDto("Conduite de balle", 0.11),
          new ExerciseDto("Tirs au but football", 0.10),
          new ExerciseDto("Lay-up et finitions", 0.11),
          new ExerciseDto("Deplacements defensifs basket", 0.12),
          new ExerciseDto("Uchi-komi", 0.14),
          new ExerciseDto("Ne-waza", 0.13),
          new ExerciseDto("Enchainements coups de pied", 0.14),
          new ExerciseDto("Poomsae", 0.10),
          new ExerciseDto("Kihon karate", 0.10),
          new ExerciseDto("Kumite", 0.15),
          new ExerciseDto("Sac de frappe", 0.16),
          new ExerciseDto("Rounds de sparring", 0.18),
          new ExerciseDto("Fentes escrime", 0.09),
          new ExerciseDto("Assauts fleuret", 0.11),
          new ExerciseDto("Amenees au sol", 0.15),
          new ExerciseDto("Lutte au sol", 0.14),
          new ExerciseDto("Descente piste", 0.12),
          new ExerciseDto("Slalom", 0.13),
          new ExerciseDto("Lancer de pierre", 0.05),
          new ExerciseDto("Balayage curling", 0.07),
          new ExerciseDto("Patinage avec crosse", 0.13),
          new ExerciseDto("Tirs au but hockey", 0.12),
          new ExerciseDto("Depart pousse luge", 0.08),
          new ExerciseDto("Descente luge", 0.07),
          new ExerciseDto("Poussee depart bobsleigh", 0.13),
          new ExerciseDto("Pilotage piste glace", 0.08),
          new ExerciseDto("Tricks flatground", 0.09),
          new ExerciseDto("Pumptrack", 0.10),
          new ExerciseDto("Tir carabine", 0.04),
          new ExerciseDto("Tir pistolet", 0.04),
          new ExerciseDto("Volee de fleches", 0.05),
          new ExerciseDto("Ancrage et visee", 0.04),
          new ExerciseDto("Tir sur plateau", 0.05),
          new ExerciseDto("Tir debout controle", 0.04),
          new ExerciseDto("Installation du spot", 0.05),
          new ExerciseDto("Repassage chronometre", 0.06),
          new ExerciseDto("Enchainement au sol", 0.09),
          new ExerciseDto("Poutre", 0.08),
          new ExerciseDto("Laser-run", 0.14),
          new ExerciseDto("Parcours epee", 0.10),
          new ExerciseDto("Transition velo course", 0.08),
          new ExerciseDto("Enchainement natation velo", 0.13),
          new ExerciseDto("Tours rapides", 0.07),
          new ExerciseDto("Freinage circuit", 0.06),
          new ExerciseDto("Virages circuit moto", 0.08),
          new ExerciseDto("Freinage urgence moto", 0.07),
          new ExerciseDto("Rame vers le line-up", 0.08),
          new ExerciseDto("Take-off", 0.09),
          new ExerciseDto("Virement de bord", 0.06),
          new ExerciseDto("Reglage de voile", 0.05),
          new ExerciseDto("Trot assis", 0.08),
          new ExerciseDto("Saut d'obstacle", 0.10),
          new ExerciseDto("Progression en chatiere", 0.11),
          new ExerciseDto("Descente en rappel speleo", 0.10),
          new ExerciseDto("Immersion controlee", 0.09),
          new ExerciseDto("Palmage", 0.10));

  private static final List<ExerciseLinkDto> EXERCISE_LINKS =
      List.of(
          new ExerciseLinkDto(
              "Course", "Course continue", "Sprint", "Montee de cote", "Burpees", "Circuit cardio"),
          new ExerciseLinkDto("Parkour", "Sprint", "Burpees", "Tractions", "Squat"),
          new ExerciseLinkDto("Callisthenie", "Pompes", "Squat", "Tractions", "Burpees", "Gainage"),
          new ExerciseLinkDto("Escalade", "Voie de vitesse", "Tractions", "Escalade bloc"),
          new ExerciseLinkDto("Natation", "Nage libre", "Crawl"),
          new ExerciseLinkDto("Plongee", "Immersion controlee", "Palmage", "Gainage"),
          new ExerciseLinkDto("Football", "Conduite de balle", "Tirs au but football", "Sprint"),
          new ExerciseLinkDto("Cyclisme", "Pedalage endurance", "Gainage"),
          new ExerciseLinkDto(
              "Basketball", "Lay-up et finitions", "Deplacements defensifs basket", "Sprint"),
          new ExerciseLinkDto(
              "Tennis", "Service tennis", "Echanges fond de court", "Travail des appuis"),
          new ExerciseLinkDto(
              "Musculation", "Developpe couche", "Developpe militaire", "Squat", "Tractions"),
          new ExerciseLinkDto("Yoga", "Yoga flow", "Equilibre", "Gainage"),
          new ExerciseLinkDto(
              "Randonnee", "Marche sur sentier", "Randonnee avec denivele", "Marche nordique"),
          new ExerciseLinkDto(
              "Alpinisme", "Approche alpine", "Progression encordee", "Marche sur sentier"),
          new ExerciseLinkDto("Lance", "Lancer de javelot", "Lancer de poids", "Gainage"),
          new ExerciseLinkDto("Marathon", "Course continue", "Montee de cote"),
          new ExerciseLinkDto("Marche", "Marche sur sentier", "Equilibre"),
          new ExerciseLinkDto("Saut", "Saut en hauteur", "Saut en longueur", "Saut technique"),
          new ExerciseLinkDto("CrossFit", "Circuit cardio", "Burpees", "Pompes", "Squat"),
          new ExerciseLinkDto("Saut_Parachute", "Sortie avion", "Atterrissage de precision"),
          new ExerciseLinkDto("Base_Jump", "Exit falaise", "Pilotage voile base jump"),
          new ExerciseLinkDto("Ping_Pong", "Service et remise", "Echanges coup droit revers"),
          new ExerciseLinkDto("Squash", "Rally squash", "Deplacements lateraux squash"),
          new ExerciseLinkDto("Judo", "Uchi-komi", "Ne-waza", "Randori"),
          new ExerciseLinkDto("Taekwondo", "Enchainements coups de pied", "Poomsae"),
          new ExerciseLinkDto("Karate", "Kihon karate", "Kumite", "Kata technique"),
          new ExerciseLinkDto("Boxe", "Sac de frappe", "Rounds de sparring", "Shadow boxing"),
          new ExerciseLinkDto("Escrime", "Fentes escrime", "Assauts fleuret"),
          new ExerciseLinkDto("Lutte", "Amenees au sol", "Lutte au sol", "Randori"),
          new ExerciseLinkDto("Ski", "Descente piste", "Slalom"),
          new ExerciseLinkDto("Curling", "Lancer de pierre", "Balayage curling"),
          new ExerciseLinkDto("Hockey", "Patinage avec crosse", "Tirs au but hockey"),
          new ExerciseLinkDto("Luge", "Depart pousse luge", "Descente luge"),
          new ExerciseLinkDto("Patinage", "Glisse endurance", "Equilibre"),
          new ExerciseLinkDto("Bobsleigh", "Poussee depart bobsleigh", "Pilotage piste glace"),
          new ExerciseLinkDto("Skate", "Tricks flatground", "Pumptrack"),
          new ExerciseLinkDto("Tir_Sportif", "Tir carabine", "Tir pistolet"),
          new ExerciseLinkDto("Tir_Arc", "Volee de fleches", "Ancrage et visee"),
          new ExerciseLinkDto("Tir_Cible", "Tir sur plateau", "Tir debout controle"),
          new ExerciseLinkDto("Repassage_Extrem", "Installation du spot", "Repassage chronometre"),
          new ExerciseLinkDto("Gymnastique", "Enchainement au sol", "Poutre", "Equilibre"),
          new ExerciseLinkDto(
              "Pentathlon", "Laser-run", "Parcours epee", "Nage libre", "Course continue"),
          new ExerciseLinkDto(
              "Triathlon",
              "Transition velo course",
              "Enchainement natation velo",
              "Pedalage endurance"),
          new ExerciseLinkDto("Formule_1", "Tours rapides", "Freinage circuit"),
          new ExerciseLinkDto("Motocyclisme", "Virages circuit moto", "Freinage urgence moto"),
          new ExerciseLinkDto("Aviron", "Rame endurance", "Gainage"),
          new ExerciseLinkDto("Canoe_Kayak", "Pagaie endurance", "Gainage"),
          new ExerciseLinkDto("Surf", "Rame vers le line-up", "Take-off"),
          new ExerciseLinkDto("Voile", "Virement de bord", "Reglage de voile"),
          new ExerciseLinkDto("Equitation", "Trot assis", "Saut d'obstacle"),
          new ExerciseLinkDto(
              "Speleologie", "Progression en chatiere", "Descente en rappel speleo"));

  private ReferenceSportExerciseCatalog() {}

  static SportExercisesDto seed(
      SportRepository sportRepository, ExerciseRepository exerciseRepository) {
    Map<String, Sport> sportsByName = new LinkedHashMap<>();
    for (SportDto dto : SPORTS) {
      sportsByName.put(dto.name(), new Sport(dto.name(), dto.met()));
    }

    List<Sport> allSports = new ArrayList<>(sportsByName.values());
    sportRepository.saveAll(allSports);

    Map<String, Exercise> exercisesByName = new LinkedHashMap<>();
    for (ExerciseDto dto : EXERCISES) {
      exercisesByName.put(dto.name(), new Exercise(dto.name(), dto.caloriesPerSecond()));
    }

    for (ExerciseLinkDto link : EXERCISE_LINKS) {
      Sport sport = requireSport(sportsByName, link.sportName());
      for (String exerciseName : link.exerciseNames()) {
        linkExercise(sport, requireExercise(exercisesByName, exerciseName));
      }
    }

    exerciseRepository.saveAll(exercisesByName.values());
    sportRepository.saveAll(allSports);
    return new SportExercisesDto(
        List.copyOf(allSports), Map.copyOf(sportsByName), Map.copyOf(exercisesByName));
  }

  private static Sport requireSport(Map<String, Sport> sportsByName, String sportName) {
    Sport sport = sportsByName.get(sportName);
    if (sport == null) {
      throw new IllegalStateException("Sport de demo introuvable: " + sportName);
    }
    return sport;
  }

  private static Exercise requireExercise(
      Map<String, Exercise> exercisesByName, String exerciseName) {
    Exercise exercise = exercisesByName.get(exerciseName);
    if (exercise == null) {
      throw new IllegalStateException("Exercice de demo introuvable: " + exerciseName);
    }
    return exercise;
  }

  private static void linkExercise(Sport sport, Exercise exercise) {
    if (!sport.getExercises().contains(exercise)) {
      sport.getExercises().add(exercise);
    }
    if (!exercise.getSports().contains(sport)) {
      exercise.getSports().add(sport);
    }
  }

  record SportExercisesDto(
      List<Sport> allSports,
      Map<String, Sport> sportsByName,
      Map<String, Exercise> exercisesByName) {

    Sport sport(String name) {
      return requireSport(sportsByName, name);
    }

    Exercise exercise(String name) {
      return requireExercise(exercisesByName, name);
    }
  }

  private record SportDto(String name, double met) {}

  private record ExerciseDto(String name, double caloriesPerSecond) {}

  private record ExerciseLinkDto(String sportName, String... exerciseNames) {
    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof ExerciseLinkDto that)) {
        return false;
      }
      return Objects.equals(sportName, that.sportName)
          && Arrays.equals(exerciseNames, that.exerciseNames);
    }

    @Override
    public int hashCode() {
      return 31 * Objects.hash(sportName) + Arrays.hashCode(exerciseNames);
    }

    @Override
    public String toString() {
      return "ExerciseLinkDto[sportName="
          + sportName
          + ", exerciseNames="
          + Arrays.toString(exerciseNames)
          + "]";
    }
  }
}
