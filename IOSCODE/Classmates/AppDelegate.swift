//
//  AppDelegate.swift
//  Classmates
//
//  Created by Mallu on 6/15/16.
//  Copyright © 2016 Mallu. All rights reserved.
//

import UIKit
import FBSDKCoreKit
import BRYXBanner

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {

    var window: UIWindow?


    func application(application: UIApplication, didFinishLaunchingWithOptions launchOptions: [NSObject: AnyObject]?) -> Bool {
        IQKeyboardManager.sharedManager().enable = true
        
        // PUSH NOTIFICATION
        let deviceToken = defaults.objectForKey("DeviceToken") as! String?
        
        if (deviceToken == nil) {
            print("There is no deviceToken saved yet.")
            let type: UIUserNotificationType = [UIUserNotificationType.Badge, UIUserNotificationType.Alert, UIUserNotificationType.Sound]
            let setting = UIUserNotificationSettings(forTypes: type, categories: nil)
            UIApplication.sharedApplication().registerUserNotificationSettings(setting)
            UIApplication.sharedApplication().registerForRemoteNotifications()
        }
      //defaults.setObject("42", forKey: "userID")
        if defaults.stringForKey("userID") != nil{
           
            let sb: UIStoryboard = UIStoryboard(name: "Main", bundle: nil)
            let destObj: DialVC = sb.instantiateViewControllerWithIdentifier("DialVC") as! DialVC
            let navController = UINavigationController(rootViewController: destObj)
            navController.navigationBarHidden = true
            
            self.window!.rootViewController = navController
        }
        
        if (launchOptions != nil) {
            self.application(application, didReceiveRemoteNotification: launchOptions!)
        }
        UIApplication.sharedApplication().applicationIconBadgeNumber = 1
        UIApplication.sharedApplication().applicationIconBadgeNumber = 0
        UIApplication.sharedApplication().cancelAllLocalNotifications()
        return true
    }
    
    func application(application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: NSData) {
        print("Got token data! \(deviceToken)")
        let characterSet: NSCharacterSet = NSCharacterSet( charactersInString: "<>" )
        
        let deviceTokenString: String = ( deviceToken.description as NSString )
            .stringByTrimmingCharactersInSet( characterSet )
            .stringByReplacingOccurrencesOfString( " ", withString: "" ) as String
        
        print(deviceTokenString)
        defaults.setObject(deviceTokenString, forKey: "DeviceToken")
    }
    
    func application(application: UIApplication, didReceiveRemoteNotification userInfo: [NSObject : AnyObject]) {
        print(userInfo["aps"])
        
        if application.applicationState == .Inactive{
            print("inactive")
            handleTap()
            //let rootViewController = self.window!.rootViewController as! UINavigationController
            //let mainStoryboard: UIStoryboard = UIStoryboard(name: "Main", bundle: nil)
            //let profileViewController = mainStoryboard.instantiateViewControllerWithIdentifier("GroupMessagesVC") as! GroupMessagesVC
            //rootViewController.pushViewController(profileViewController, animated: true)
        }
        if application.applicationState == .Active{
                        
            if shouldShow==true {
                let banner = Banner(title: "ClassMates", subtitle: "You have a new message", image: UIImage(named: "Icon"), backgroundColor: colorLightBlue)
                banner.dismissesOnTap = false
                banner.show(duration: 3.0)
                let tap = UITapGestureRecognizer(target: self, action: #selector(handleTap))
                banner.addGestureRecognizer(tap)
            }
        }
    }
    func handleTap(){
        let rootViewController = self.window!.rootViewController as! UINavigationController
        let mainStoryboard: UIStoryboard = UIStoryboard(name: "Main", bundle: nil)
        let profileViewController = mainStoryboard.instantiateViewControllerWithIdentifier("GroupMessagesVC") as! GroupMessagesVC
        rootViewController.pushViewController(profileViewController, animated: true)
    }
    func application(application: UIApplication, didFailToRegisterForRemoteNotificationsWithError error: NSError) {
        print(error)
    }
    func applicationWillResignActive(application: UIApplication) {
        // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
        // Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
    }
    func applicationDidEnterBackground(application: UIApplication) {
        // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
        // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
    }
    func applicationWillEnterForeground(application: UIApplication) {
        // Called as part of the transition from the background to the inactive state; here you can undo many of the changes made on entering the background.
        if UIApplication.sharedApplication().applicationIconBadgeNumber == 0 {
            UIApplication.sharedApplication().applicationIconBadgeNumber = 1
        }
    }

    func applicationDidBecomeActive(application: UIApplication) {
        // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
        FBSDKAppEvents.activateApp()
        UIApplication.sharedApplication().applicationIconBadgeNumber = 0
        UIApplication.sharedApplication().cancelAllLocalNotifications()
    }
    func application(application: UIApplication, openURL url: NSURL, sourceApplication: String?, annotation: AnyObject) -> Bool {
        return FBSDKApplicationDelegate.sharedInstance().application(application, openURL: url, sourceApplication: sourceApplication, annotation: annotation)
    }
    func applicationWillTerminate(application: UIApplication) {
        // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
    }


}

