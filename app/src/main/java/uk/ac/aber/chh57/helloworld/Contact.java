package uk.ac.aber.chh57.helloworld;

/**
 * Created by Christian on 05/12/2014.
 */
public class Contact {

    private String _name, _phone, _email;

    public Contact (String name, String email, String phone){
        _name = name;
        _phone = phone;
        _email = email;
    }

    public String getName(){
        return _name;
    }

    public String getPhone(){
        return _phone;
    }

    public String getEmail(){
        return _email;
    }

}
