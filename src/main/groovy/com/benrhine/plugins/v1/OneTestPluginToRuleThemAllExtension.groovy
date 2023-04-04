package com.benrhine.plugins.v1

/** --------------------------------------------------------------------------------------------------------------------
 * SlackAlertsPluginExtension: Global config that will allow for the setting of common variables a single time.
 * ------------------------------------------------------------------------------------------------------------------ */
class OneTestPluginToRuleThemAllExtension {
    transient String environment
    transient String webHook
    transient String uploadUrl
    transient String token
    transient String channels
    transient String payload
    transient String integrationPath
    transient String smokePath
    transient String topPackageName = "com/benrhine/SpringBoot2022Example/v1"
    boolean enableIntegration = false
    boolean enableSmoke = false
    boolean allowFullPath = false
    boolean includeAuthenticated = true
    boolean includeUnauthenticated = true
    boolean includeValidation = true
    boolean displayLogging = false

}
