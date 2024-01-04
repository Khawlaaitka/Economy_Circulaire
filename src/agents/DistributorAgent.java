package agents;

import jade.gui.AgentWindowed;
import jade.gui.SimpleWindow4Agent;

import java.awt.*;

public class DistributorAgent extends AgentWindowed {

    protected void setup() {
        initializeAgent();
    }

    private void initializeAgent() {
        this.window = new SimpleWindow4Agent(getLocalName(), this);
        window.setBackgroundTextColor(Color.CYAN);
        println("Hello, I am a distributor agent.");
    }

    @Override
    public void takeDown() {
        println("Distributor-agent " + getAID().getName() + " terminating.");
    }

    public static void main(String[] args) {
        new DistributorAgent().setup();
}
}