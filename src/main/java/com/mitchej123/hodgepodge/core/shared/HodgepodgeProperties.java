package com.mitchej123.hodgepodge.core.shared;

public final class HodgepodgeProperties {

    public static final boolean LOG_CONFIG_PARSING_TIMES = Boolean.getBoolean("hodgepodge.logConfigTimes");
    public static final boolean LOG_ENUM_VALUES = Boolean.getBoolean("hodgepodge.logEnumValues");
    public static final boolean LOG_STACKTRACE_ENUM_VALUES = Boolean.getBoolean("hodgepodge.logStacktraceEnumValues");
    public static final boolean DUMP_ASM_CLASS = Boolean.getBoolean("hodgepodge.dumpClass");
    public static final boolean LOG_EVENT_TIMES = Boolean.getBoolean("hodgepodge.logEventTimes");
    public static final boolean DEBUG_TEXTURES = Boolean.getBoolean("hodgepodge.debugtextures");
    public static final boolean LOG_MOD_TIMES = Boolean.getBoolean("hodgepodge.logModTimes");
    public static final boolean ASSERT_MAP_THREAD_OWNER = Boolean.getBoolean("hodgepodge.assertMapThreadOwner");

    private HodgepodgeProperties() {}
}
