import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

class HelloWorldTask extends DefaultTask {
    @Optional
    String message = 'I am kaku'

    @TaskAction
    def hello() {
        println "hello world $message"
    }
}