package org.prog3.email.client.model.tasks;

public class AskAccount extends ClientTask {
    public AskAccount() {

    }

    @Override
    public void run() {
        controller.promptForAccount();
    }
}
