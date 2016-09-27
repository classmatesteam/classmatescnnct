//
//  TopicVC.swift
//  Classmates
//
//  Created by Mallu on 6/18/16.
//  Copyright © 2016 Mallu. All rights reserved.
//

import Foundation
import UIKit
class TopicVC: UIViewController, UITextFieldDelegate{
    
   
    @IBOutlet weak var viewBack: UIView!
    @IBOutlet weak var viewTop: UIView!
    @IBOutlet weak var txtTopicName: UITextField!
    @IBOutlet weak var lblHint: UILabel!
    @IBOutlet weak var lblHint2: UILabel!
    @IBOutlet weak var lblHint3: UILabel!
    
    @IBOutlet weak var btnNext: ConfigureButton!
    var courseName = ""
    
    override func viewDidLoad() {
        super.viewDidLoad()
        configureDesign()
    }
    func configureDesign(){
       
        //myMutableString = NSMutableAttributedString(string: lblHint.text!, attributes: [NSFontAttributeName:UIFont(name: "Ubuntu", size: 12.0)!])
        //myMutableString.addAttribute(NSForegroundColorAttributeName, value: colorBottom, range: NSRange(location:24,length:8))
        //lblHint.attributedText = myMutableString
        lblHint.sizeToFit()
        lblHint2.sizeToFit()
        lblHint3.sizeToFit()
        lblHint2.frame.origin.x = lblHint.frame.origin.x + lblHint.frame.size.width
         lblHint3.frame.origin.x = lblHint2.frame.origin.x + lblHint2.frame.size.width
        initAppearance(btnNext)
        self.viewBack.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(CourseVC.actionBack)))
        txtTopicName.attributedPlaceholder = NSAttributedString(string: "Topic Name", attributes: [NSForegroundColorAttributeName: UIColor.whiteColor()])
        txtTopicName.delegate = self
    }
    @IBAction func actionNext(sender: AnyObject) {
       next()        
    }
    func textFieldShouldReturn(textField: UITextField) -> Bool {
        next()
        return true
    }
    func next(){
        counter = range(txtTopicName)
        if counter.characters.count == 0 {
            self.simpleAlert("Alert", message: "Please Enter Topic Name.")
        }else{
            self.performSegueWithIdentifier("segueLocation", sender: self)
        }
        
        //self.navigationController?.pushViewController(self.storyboard?.instantiateViewControllerWithIdentifier("LocationVC") as! LocationVC, animated: true)
    }
    override func touchesBegan(touches: Set<UITouch>, withEvent event: UIEvent?) {
        self.view.endEditing(true)
    }
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        let nextVC = segue.destinationViewController as! LocationVC
        nextVC.courseName = courseName
        nextVC.topicName = txtTopicName.text!
        //backItem.title = "Create New Event"
        //self.navigationItem.backBarButtonItem = backItem
    }
    override func preferredStatusBarStyle() -> UIStatusBarStyle {
        return .LightContent
    }
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
}