# easy-slackbot
 [![Release](https://img.shields.io/jitpack/v/yusaka39/easy-slackbot.svg)](https://jitpack.io/#yusaka39/easy-slackbot)
[![CircleCI Build Status](https://img.shields.io/circleci/project/github/yusaka39/easy-slackbot/master.svg)](https://circleci.com/gh/yusaka39/easy-slackbot)
[![Codecov Coverage](https://img.shields.io/codecov/c/github/yusaka39/easy-slackbot/develop.svg)](https://codecov.io/gh/yusaka39/easy-slackbot)

An easy way to build your own slack bot on Kotlin/JVM.

# Getting started
Buildable sample is [here](./sample)

## Configure project
Add `easy-slackbot` and `slf4j-api` as a dependency. A gradle example is below.

```gradle
repositories {
    ...
    maven { url 'https://jitpack.io' }
}

dependencies {
    compile 'com.github.yusaka39:easy-slackbot:0.3.1-1'
    compile 'org.slf4j:slf4j-api:1.7.25' // Required
    compile 'org.slf4j:slf4j-simple:1.7.25' // Optional
}
```

If you want to see logs, you need add a slf4j binder as a dependency. In example, `slf4j-simple` is added as a binder.

You can see full `build.gradle` [here](./sample/build.gradle)

## Code a bot
It is easy to make a simple bot.

```kotlin
package io.github.yusaka39.easySlackbot.sample

import io.github.yusaka39.easySlackbot.annotations.GroupParam
import io.github.yusaka39.easySlackbot.annotations.HandlerFunction
import io.github.yusaka39.easySlackbot.annotations.RunWithInterval
import io.github.yusaka39.easySlackbot.bot.Bot
import io.github.yusaka39.easySlackbot.router.HandlerPack
import io.github.yusaka39.easySlackbot.router.actions.PostAction
import io.github.yusaka39.easySlackbot.router.actions.PostWithChannelNameAction
import io.github.yusaka39.easySlackbot.router.actions.PutReactionAction
import io.github.yusaka39.easySlackbot.router.actions.putAttachmentToChannelAction
import java.lang.management.ManagementFactory
import java.util.concurrent.TimeUnit

fun main(args: Array<String>) {
    Bot(
        /* Bot user token */ args[0],
        /* Package name to search handler functions */ "io.github.yusaka39.easySlackbot.sample"
    ).run()
}

// Classes contains handler functions must extend HandlerPack and have a primary constructor without arguments
class Handlers : HandlerPack() {
    @HandlerFunction("""^say\s+(.*)""")
    // Handler functions must return an instance of Action
    fun say(@GroupParam(1) words: String) =
        PostAction(this.receivedMessage.channel, "${this.receivedMessage.user.replyString}: $words")
    
    // You can give RegexOption
    @HandlerFunction("is released", regexOption = [RegexOption.IGNORE_CASE])
    fun congrats() =
        // Actions can be composed
        PutReactionAction(this.receivedMessage, "tada") compose PostAction(this.receivedMessage.channel, "Congrats!")

    @HandlerFunction("""^plus\s+(\d+)\s+(\d+)""")
    fun plus(@GroupParam(1) a: Int /* String will be converted automatically */, @GroupParam(2) b: Int) = 
        PostAction(this.receivedMessage.channel, "${ a + b }")

    // See also AttachmentListBuilder
    @HandlerFunction("^status$")
    fun showStatus() = putAttachmentToChannelAction(this.receivedMessage.channel) {
        attachment {
            color = "#00AA99"
            title = "Bot Status"
            field {
                title = "Status"
                value = "I'm fine"
                isShort = true
            }
            field {
                title = "Uptime"
                value = ManagementFactory.getRuntimeMXBean().uptime.toString()
                isShort = true
            }
        }
        attachment {
            color = "#DD3333"
            title = "Hanger"
            field {
                title = "Stomach"
                value = "Stomach is grumbling"
                isShort = false
            }
        }
    }

    // The bot says "Are you still working? Would you like to take a nap?" every 00:00 UTC
    @get:RunWithInterval(0, 0, "UTC", 24, TimeUnit.HOURS)
    val takeANap = PostWithChannelNameAction("general", "Are you still working? Would you like to take a nap?")
}
```

<img src="./doc/screenshot.png" width="400" />

You can see more sample [here](./sample/src/main/kotlin/io/github/yusaka39/easySlackbot/sample/Main.kt)
