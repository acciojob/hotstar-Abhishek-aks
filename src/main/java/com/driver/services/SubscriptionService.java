package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        //Save The subscription Object into the Db and return the total Amount that user has to pay
        Subscription subscription = new Subscription();

         int amountToBepaid =0;
         if(subscription.getSubscriptionType().toString().equals("BASIC")){
             amountToBepaid= 500 + (200 * subscriptionEntryDto.getNoOfScreensRequired());
         }
        if(subscription.getSubscriptionType().toString().equals("PRO")){
            amountToBepaid= 800 + (250 * subscriptionEntryDto.getNoOfScreensRequired());
        }
        if(subscription.getSubscriptionType().toString().equals("ELITE")){
            amountToBepaid= 1000 + (350 * subscriptionEntryDto.getNoOfScreensRequired());
        }

        subscription.setId(subscriptionEntryDto.getUserId());
        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());
        subscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());
        subscription.setTotalAmountPaid(amountToBepaid);

        User premiumUser = userRepository.findById(subscriptionEntryDto.getUserId()).get();
        subscription.setUser(premiumUser);

        // parrent is user
        userRepository.save(premiumUser);

        return amountToBepaid;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository

        User user = userRepository.findById(userId).get();
        Subscription userSubscription = user.getSubscription();

        if(userSubscription.getSubscriptionType().toString().equals("ELITE")){
            throw new Exception("Best Subscription already present");
        }

        int prevPaid = user.getSubscription().getTotalAmountPaid();
        int currPaid = 0;

        if(userSubscription.getSubscriptionType().toString().equals("BASIC")){
            currPaid = 800 + (250 * user.getSubscription().getNoOfScreensSubscribed());
            userSubscription.setSubscriptionType(SubscriptionType.PRO);
        }
        else{
            currPaid = 1000 + (350 * user.getSubscription().getNoOfScreensSubscribed());
            userSubscription.setSubscriptionType(SubscriptionType.ELITE);
        }

        subscriptionRepository.save(userSubscription);

        return currPaid-prevPaid;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb

        List<Subscription> subscriptions = subscriptionRepository.findAll();
        int revenue = 0; // retyrn type is wrapper class
//        for(int i =0; i< subscriptions.size();i++)
//        {
//            revenue +=subscriptions.get(i).getTotalAmountPaid();
//        }
        for(Subscription subscription : subscriptions){
            revenue += subscription.getTotalAmountPaid();
        }
        return revenue;
    }

}
