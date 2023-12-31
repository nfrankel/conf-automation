package ch.frankel.conf.automation

import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener


class LogAppVersionStartupListener(private val version: String) {

    private val logger = LoggerFactory.getLogger(LogAppVersionStartupListener::class.java)

    @EventListener
    fun onStartup(event: ApplicationReadyEvent) {
        logger.info("Starting application v$version")
    }
}
