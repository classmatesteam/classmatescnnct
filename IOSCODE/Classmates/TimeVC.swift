//
//  TimeVC.swift
//  Classmates
//
//  Created by Mallu on 6/18/16.
//  Copyright © 2016 Mallu. All rights reserved.
//

import Foundation
import UIKit
import Alamofire
class TimeVC: UIViewController, UITextFieldDelegate{
    
    @IBOutlet weak var viewTop: UIView!
    @IBOutlet weak var viewBack: UIView!
    @IBOutlet weak var txtTime: UITextField!
    @IBOutlet weak var lblHint: UILabel!
    @IBOutlet weak var btnNext: ConfigureButton!
    //var userID = ""
    var courseName = ""
    var topicName = ""
    var location = ""
    var day = ""
    var month = ""
    var year = ""
    var hr = ""
    var min = ""
    var sec = ""
    var ap = ""
    var str = ""
    var datePickerView  : UIDatePicker = UIDatePicker()
     let timeFormatter = NSDateFormatter()
    var dateSelected: NSDate!
    //var z = ""
    override func viewDidLoad() {
        super.viewDidLoad()
        configureDesign()
        txtTime.delegate = self
        
    }
    func configureDesign(){
        lblHint.hidden = true
        myMutableString = NSMutableAttributedString(string: lblHint.text!, attributes: [NSFontAttributeName:UIFont(name: "Ubuntu", size: 12.0)!])
        myMutableString.addAttribute(NSForegroundColorAttributeName, value: colorDarkBlue, range: NSRange(location:19,length:8))
        lblHint.attributedText = myMutableString
        initAppearance(btnNext)
        //initAppearNavigation(viewTop)
        viewBack.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(ClassNumberVC.actionBack)))
        txtTime.attributedPlaceholder = NSAttributedString(string: "Start Time", attributes: [NSForegroundColorAttributeName: UIColor.whiteColor()])
        userID = defaults.stringForKey("userID")!
        
       
        datePickerView.datePickerMode = UIDatePickerMode.DateAndTime
        let date = NSDate()
        print(date)
        handleDatePicker()
        datePickerView.minimumDate = date
        txtTime.inputView = datePickerView
        datePickerView.addTarget(self, action: #selector(TimeVC.handleDatePicker), forControlEvents: UIControlEvents.ValueChanged)
        txtTime.delegate = self
    }
    @IBAction func actionNext(sender: AnyObject) {
       done()
    }
    func textFieldShouldReturn(textField: UITextField) -> Bool {
        done()
        return true
    }
    func done(){
        counter = range(txtTime)
        if counter.characters.count == 0 {
            self.simpleAlert("Alert", message: "Please Select Time.")
        }else{
            self.apiAddStudy()
          
        }
            
    }
    func apiAddStudy(){
        print(str)
         let data = [
         "userid" : userID,
         "name" : courseName,
         "tname" : topicName,
         "location" : location,
         "time" : str,
         "time_zone" : defaults.stringForKey("timeZone")!,
         "Addevent" : ""
         ]
         print(data)
         self.showHUD()
         Alamofire.request(.POST, apiLink, parameters: data)
         .responseJSON {response in
         //print(response.result)
         if String(response.result) == "SUCCESS"{
         if let JSON = response.result.value {
            print("JSON: \(JSON)")
            apiStatus = JSON["status"] as! String
            if apiStatus == "success" {
                self.hideHUD()
         self.navigationController?.pushViewController(self.storyboard?.instantiateViewControllerWithIdentifier("DialVC") as! DialVC, animated: true)
         }else{
            self.hideHUD()
            self.simpleAlert("Alert", message: "Topic Name Already Created. Please Try Different.")
         }
         }
         }else{
            self.hideHUD()
            self.simpleAlert("Network Error", message: "Please check your internet connection...")
         }
         }//end api
    }
    func handleDatePicker(){
        let date = NSDate()
        //newDate1 = date!.dateByAddingTimeInterval(60 * 60 * 24 * 1)
        datePickerView.minimumDate = date
       
        timeFormatter.dateFormat = "MM/dd/yyyy hh:mm a"
        //timeFormatter.timeStyle = ""
        txtTime.text = timeFormatter.stringFromDate(datePickerView.date)
        
        dateSelected = datePickerView.date
        
        timeFormatter.dateFormat = "dd"
        day = timeFormatter.stringFromDate(datePickerView.date)
        timeFormatter.dateFormat = "MM"
        month = timeFormatter.stringFromDate(datePickerView.date)
        timeFormatter.dateFormat = "yyyy"
        year = timeFormatter.stringFromDate(datePickerView.date)
        timeFormatter.dateFormat = "hh"
        hr = timeFormatter.stringFromDate(datePickerView.date)
        timeFormatter.dateFormat = "mm"
        min = timeFormatter.stringFromDate(datePickerView.date)
        timeFormatter.dateFormat = "ss"
        sec = timeFormatter.stringFromDate(datePickerView.date)
        timeFormatter.dateFormat = "a"
        ap = timeFormatter.stringFromDate(datePickerView.date)
        str = "\(year)/\(month)/\(day) \(hr):\(min):\(sec) \(ap)"
      
        /*let zone = String(timeFormatter.timeZone)
       
        //let iOStimeZones = NSTimeZone.knownTimeZoneNames()
        for index in zone.startIndex..<zone.endIndex {
            if "\(zone[index])" == " " {
                return
            }else{
                z = "\(z)\(zone[index])"
            }
        }*/
    }
    override func touchesBegan(touches: Set<UITouch>, withEvent event: UIEvent?) {
        self.view.endEditing(true)
    }
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
       // backItem.title = "Create New Event"
        //self.navigationItem.backBarButtonItem = backItem
    }
    override func preferredStatusBarStyle() -> UIStatusBarStyle {
        return .LightContent
    }
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
}
extension String {
    func insert(string:String,ind:Int) -> String {
        return  String(self.characters.prefix(ind)) + string + String(self.characters.suffix(self.characters.count-ind))
    }
}
extension NSDate {
    func isGreaterThanDate(dateToCompare: NSDate) -> Bool {
        //Declare Variables
        var isGreater = false
        
        //Compare Values
        if self.compare(dateToCompare) == NSComparisonResult.OrderedDescending {
            isGreater = true
        }
        
        //Return Result
        return isGreater
    }
    func equalToDate(dateToCompare: NSDate) -> Bool {
        //Declare Variables
        var isEqualTo = false
        
        //Compare Values
        if self.compare(dateToCompare) == NSComparisonResult.OrderedSame {
            isEqualTo = true
        }
        
        //Return Result
        return isEqualTo
    }
}


//var str: String = txtTime.text!
// str = str.insert(":00", ind: 16)
/* let currentDateTime = NSDate()
 
 if((dateSelected).isGreaterThanDate(currentDateTime)) {
 //Do Something...
 print("greater")
 apiAddStudy()
 
 }else{
 if((dateSelected).isEqualToDate(currentDateTime)) {
 print("equal")
 apiAddStudy()
 }else{
 print("less")
 }
 }
 */