//
//  LocationVC.swift
//  Classmates
//
//  Created by Mallu on 6/18/16.
//  Copyright © 2016 Mallu. All rights reserved.
//

import Foundation
import UIKit
class LocationVC: UIViewController, UITextFieldDelegate{
    // UIPickerViewDelegate, UIPickerViewDataSource,
  
    @IBOutlet weak var viewTop: UIView!
    @IBOutlet weak var viewBack: UIView!
    @IBOutlet weak var txtLocation: UITextField!
    @IBOutlet weak var lblHint: UILabel!
    
    @IBOutlet weak var lblHint2: UILabel!
    @IBOutlet weak var btnNext: ConfigureButton!
    
    @IBOutlet weak var lblHint3: UILabel!
    var courseName = ""
    var topicName = ""
    //var arrLocation = ["Computer Lab", "Covocation Hall", "Hayden Lawn", "Hayden Library 2nd floor", "Multi Purpose Hall"]
    override func viewDidLoad() {
        super.viewDidLoad()
        configureDesign()
     
    }
    func configureDesign(){
        
        //myMutableString = NSMutableAttributedString(string: lblHint.text!, attributes: [NSFontAttributeName:UIFont(name: "Ubuntu", size: 12.0)!])
        //myMutableString.addAttribute(NSForegroundColorAttributeName, value: colorBottom, range: NSRange(location:23,length:14))
        //lblHint.attributedText = myMutableString
        lblHint.sizeToFit()
        lblHint2.sizeToFit()
        lblHint3.sizeToFit()
        lblHint2.frame.origin.x = lblHint.frame.origin.x + lblHint.frame.size.width
        lblHint3.frame.origin.x = lblHint2.frame.origin.x + lblHint2.frame.size.width
        initAppearance(btnNext)
        //initAppearNavigation(viewTop)
        viewBack.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(ClassNumberVC.actionBack)))
        txtLocation.attributedPlaceholder = NSAttributedString(string: "Location", attributes: [NSForegroundColorAttributeName: UIColor.whiteColor()])
       
       // let pickerView = UIPickerView()
       
        //pickerView.delegate = self
        
        //txtLocation.inputView = pickerView
       
        txtLocation.delegate = self
    }
    @IBAction func actionNext(sender: AnyObject) {
        next()
    }
    func textFieldShouldReturn(textField: UITextField) -> Bool {
        next()
        return true
    }
    func next(){
        counter = range(txtLocation)
        if counter.characters.count == 0 {
            self.simpleAlert("Alert", message: "Please Enter Location.")
        }else{
            self.performSegueWithIdentifier("segueTime", sender: self)
        }
       
    }
   /* func numberOfComponentsInPickerView(pickerView: UIPickerView) -> Int {
        return 1
    }
    func pickerView(pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int{
        return arrLocation.count
    }
    func pickerView(pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String? {
        return arrLocation[row] 
    }
    
    func pickerView(pickerView: UIPickerView, didSelectRow row: Int, inComponent component: Int) {
        txtLocation.text = arrLocation[row]
        //typeBarButton.title = array[row]["type1"] as? String
        //typePickerView.hidden = false
    }
    func pickerView(pickerView: UIPickerView, viewForRow row: Int, forComponent component: Int, reusingView view: UIView?) -> UIView {
        var pickerLabel = view as! UILabel!
        if view == nil {
            pickerLabel = UILabel()
            pickerLabel.textColor = UIColor.blackColor()
           // pickerLabel.backgroundColor = UIColor.clearColor()
            pickerLabel.textAlignment = NSTextAlignment.Center
            pickerLabel.text = arrLocation[row]
            pickerLabel.font = UIFont(name: "Ubuntu", size: 17)
        }
        return pickerLabel
    }*/
    override func touchesBegan(touches: Set<UITouch>, withEvent event: UIEvent?) {
        self.view.endEditing(true)
    }
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        let nextVC = segue.destinationViewController as! TimeVC
        nextVC.courseName = courseName
        nextVC.topicName = topicName
        nextVC.location = txtLocation.text!
        //backItem.title = "Create New Event"
       // self.navigationItem.backBarButtonItem = backItem
    }
    override func preferredStatusBarStyle() -> UIStatusBarStyle {
        return .LightContent
    }
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
}