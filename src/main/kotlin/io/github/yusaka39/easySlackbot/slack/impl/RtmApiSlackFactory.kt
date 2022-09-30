import io.github.yusaka39.easySlackbot.slack.Slack
import io.github.yusaka39.easySlackbot.slack.SlackFactory
import io.github.yusaka39.easySlackbot.slack.impl.RtmApiSlack

class RtmApiSlackFactory : SlackFactory {
    override fun create(slackToken: String): Slack = RtmApiSlack(slackToken)
}
