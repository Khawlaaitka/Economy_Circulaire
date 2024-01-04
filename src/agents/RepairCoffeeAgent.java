package agents;

import data.ProductType;
import jade.core.AgentServicesTools;
import jade.core.behaviours.CyclicBehaviour;
import jade.gui.AgentWindowed;
import jade.gui.SimpleWindow4Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.time.LocalDate;

import java.util.ArrayList;
import java.util.List;
import java.awt.Color;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/** class that represents a repair agent.
 * It is declared in the service repair-coffee.
 * It owns some specialities
 * @author emmanueladam@
 * */
public class RepairCoffeeAgent extends AgentWindowed {
    List<ProductType> specialities;
    private LocalDate DateRand() {
        LocalDate now = LocalDate.now();
        long AddDay = ThreadLocalRandom.current().nextLong(1, 4); // Entre 1 et 3 jours (exclus)
        return now.plusDays(AddDay);
    }
    @Override
    public void setup(){
        this.window = new SimpleWindow4Agent(getLocalName(),this);
        this.window.setBackgroundTextColor(Color.orange);
        println("hello, do you want coffee ?");
        var hasard = new Random();

        specialities = new ArrayList<>();
        for(ProductType type : ProductType.values())
            if(hasard.nextBoolean()) specialities.add(type);
        //we need at least one speciality
        if(specialities.isEmpty()) specialities.add(ProductType.values()[hasard.nextInt(ProductType.values().length)]);
        println("I have these specialities : ");
        specialities.forEach(p->println("\t"+p));
        //registration to the yellow pages (Directory Facilitator Agent)
        AgentServicesTools.register(this, "repair", "coffee");
        addBehaviour(new CyclicBehaviour(this) {
            public void action() {
                // On va commencer par filtrer les messages pour avoir de ceux de types CFP
                MessageTemplate cfpMess = MessageTemplate.MatchPerformative(ACLMessage.CFP);
                // Réception du prochain cfp
                ACLMessage msgreceive = receive(cfpMess);

                if (msgreceive != null) {
                    println("Message CFP reçu: " + msgreceive.getContent());
                    String[] products = msgreceive.getContent().split(": ")[1].split(",");
                    List<ProductType> productRepaire = new ArrayList<>();

                    for (String token : products) {
                        String trimmedToken = token.trim();
                        for (ProductType type : ProductType.values()) {
                            if (type.name().equalsIgnoreCase(trimmedToken)) {
                                productRepaire.add(type);
                                break;
                            }
                        }
                    }

                    // Vérifier le type des produits reçu
                    println("Produits à réparer : ");
                    productRepaire.forEach(p -> println("\t" + p));
                    boolean mySpeciality = specialities.stream().anyMatch(productRepaire::contains);
                    if (mySpeciality) {
                        ACLMessage reply = msgreceive.createReply();
                        reply.setPerformative(ACLMessage.PROPOSE);
                        LocalDate ranDate = DateRand();
                        reply.setContent("Proposition de la date de réparation : " + ranDate);
                        send(reply);
                        //Pour vérifier l'envoi du message avec la date générer
                        println("La proposition a été envoyée avec la date : " + ranDate);

                    } else {
                    block();
                }
            }}
        });

    }

}