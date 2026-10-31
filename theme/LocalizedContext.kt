package com.aiagent.assistant.presentation.theme

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.content.res.Resources
import com.aiagent.assistant.domain.settings.AppLanguage
import java.util.Locale

/**
 * অ্যাপের ভাষা বদলানোর সহজ উপায়: Activity-কে না বদলে শুধু resources (strings) অন্য ভাষার দেওয়া হয়।
 * base Activity-ই থাকে, তাই permission dialog, Settings খোলা ইত্যাদি আগের মতো কাজ করে।
 */
class LocalizedContext private constructor(
    base: Context,
    private val localizedResources: Resources
) : ContextWrapper(base) {

    override fun getResources(): Resources = localizedResources

    companion object {
        fun create(base: Context, language: AppLanguage): Context {
            val tag = language.tag ?: return base
            val config = Configuration(base.resources.configuration)
            config.setLocale(Locale.forLanguageTag(tag))
            val localized = base.createConfigurationContext(config)
            return LocalizedContext(base, localized.resources)
        }
    }
}
