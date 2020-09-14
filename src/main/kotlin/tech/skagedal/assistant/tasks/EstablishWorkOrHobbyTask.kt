package tech.skagedal.assistant.tasks

import tech.skagedal.assistant.RunnableTask
import tech.skagedal.assistant.TaskResult
import tech.skagedal.assistant.ui.UserInterface

class EstablishWorkOrHobbyTask : RunnableTask {
    val userInterface = UserInterface()

    enum class ShiftType {
        WORK, HOBBY
    }

    override fun runTask(): TaskResult {
        val shiftType = userInterface.pickOne<ShiftType>("What shift type?") {
            choice(ShiftType.WORK, "Work")
            choice(ShiftType.HOBBY, "Hobby")
        }
        println(shiftType)
        return TaskResult.Proceed
    }
}