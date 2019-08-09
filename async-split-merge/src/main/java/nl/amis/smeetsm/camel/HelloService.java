package nl.amis.smeetsm.camel;

public class HelloService {

    public static void getGreeting(HelloBean bodyIn) {
        bodyIn.setName( "Hello, " + bodyIn.getName() );
        bodyIn.setId(bodyIn.getId()*10);
    }
}