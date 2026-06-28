package fr.kosmosuniverse.kuffle.core;

import lombok.Getter;

/**
 * @author KosmosUniverse
 */
public enum ConfigPaths {
    SYS_START_MODE("system_settings.start_mode"),
    SYS_LOG_RESULT("system_settings.log_game_results"),
    GAME_PRINT("game_settings.print_player_tab"),
    GAME_END_ONE("game_settings.end_game_when_one_remains"),
    GAME_SPREAD("game_settings.spreadplayers.enable"),
    GAME_SPREAD_DIST("game_settings.spreadplayers.minimum_distance"),
    GAME_SPREAD_RAD("game_settings.spreadplayers.minimum_radius"),
    GAME_SAT("game_settings.saturation"),
    GAME_REWARD("game_settings.rewards"),
    GAME_PASS_ALL("game_settings.passive.all"),
    GAME_PASS_TEAM("game_settings.passive.team"),
    GAME_TIME_START("game_settings.time.start"),
    GAME_TIME_ADD("game_settings.time.added"),
    GAME_LAST_AGE("game_settings.last_age"),
    GAME_NB_TARGET("game_settings.target_per_age"),
    GAME_PERS_LANG("game_settings.personals.lang"),
    GAME_PERS_TIPS("game_settings.personals.tips"),
    GAME_LEVEL("game_settings.level"),
    GAME_SKIP("game_settings.skip.enable"),
    GAME_SKIP_AGE("game_settings.skip.age"),
    GAME_CRAFTS("game_settings.custom_crafts"),
    GAME_TEAM("game_settings.team.enable"),
    GAME_TEAM_SIZE("game_settings.team.size"),
    GAME_TEAM_INV("game_settings.team.inv.enable"),
    GAME_TEAM_INV_SIZE("game_settings.team.inv.size"),
    GAME_MODE_COOP("game_settings.modes.coop.enable"),
    GAME_MODE_COOP_BASE("game_settings.modes.coop.baseValue"),
    GAME_MODE_COOP_ADDED("game_settings.modes.coop.timeAdded"),
    GAME_MODE_COOP_SKIP("game_settings.modes.coop.skipCost"),
    GAME_MODE_SAME("game_settings.modes.same"),
    GAME_MODE_DOUBLE("game_settings.modes.double"),
    GAME_MODE_SBTT("game_settings.modes.sbtt.enable"),
    GAME_MODE_SBTT_AMNT("game_settings.modes.sbtt.amount"),
    GAME_XP_END("game_settings.xp_max.end_teleporter"),
    GAME_XP_OVERWORLD("game_settings.xp_max.overworld_teleporter"),
    GAME_XP_CORAL("game_settings.xp_max.coral_compass");

    @Getter
    private final String path;

    ConfigPaths(String path) {
        this.path = path;
    }
}
