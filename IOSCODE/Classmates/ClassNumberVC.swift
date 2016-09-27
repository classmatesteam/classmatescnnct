 //
//  ClassNumberVC.swift
//  Classmates
//
//  Created by Mallu on 6/15/16.
//  Copyright © 2016 Mallu. All rights reserved.
//

import Foundation
import UIKit
class ClassNumberVC: UIViewController, UITextFieldDelegate{
    
    @IBOutlet weak var viewTop: UIView!
    @IBOutlet weak var viewBack: UIView!
    @IBOutlet weak var lblHint: UILabel!
    
    @IBOutlet weak var lblHint2: UILabel!
    @IBOutlet weak var lblHint3: UILabel!
    @IBOutlet weak var txtClassNumber: UITextField!
    
    @IBOutlet weak var btnNext: ConfigureButton!
    override func viewDidLoad() {
        super.viewDidLoad()
        
        configureDesign()
    }
    override func viewWillAppear(animated: Bool) {
        super.viewWillAppear(true)
       
      
    }
    func configureDesign(){
        self.navigationController?.navigationBar.backItem?.title = "Create New Class"
       // myMutableString = NSMutableAttributedString(string: lblHint.text!, attributes: [NSFontAttributeName:UIFont(name: "Ubuntu", size: 12.0)!])
        //myMutableString.addAttribute(NSForegroundColorAttributeName, value: colorBottom, range: NSRange(location:24,length:7))
        //lblHint.attributedText = myMutableString
        lblHint.sizeToFit()
        lblHint2.sizeToFit()
        lblHint3.sizeToFit()
        lblHint2.frame.origin.x = lblHint.frame.origin.x + lblHint.frame.size.width
        lblHint3.frame.origin.x = lblHint2.frame.origin.x + lblHint2.frame.size.width
        initAppearance(btnNext)
        //viewBack.userInteractionEnabled = true
        viewBack.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(ClassNumberVC.actionBack)))
        //self.navigationItem.backBarButtonItem = UIBarButtonItem(title: "Create New Class", style: UIBarButtonItemStyle.Plain, target: nil, action: nil)
        //txtCourseName.attributedPlaceholder = NSAttributedString(string: "Course Name", attributes: [NSForegroundColorAttributeName: UIColor.whiteColor()])
        txtClassNumber.attributedPlaceholder = NSAttributedString(string: "Class Name", attributes: [NSForegroundColorAttributeName: UIColor.whiteColor()])
        txtClassNumber.delegate = self
        txtClassNumber.autocapitalizationType = UITextAutocapitalizationType.AllCharacters
        
    }
    @IBAction func actionNext(sender: AnyObject) {
        next()
    }
    
    func textFieldShouldReturn(textField: UITextField) -> Bool {
        next()
        return true
    }
    func next(){
        counter = range(txtClassNumber)
        
        if counter.characters.count == 0{
            self.simpleAlert("Alert", message: "Please Enter Class Name.")
        }else if counter.characters.count != 6{
            self.simpleAlert("Alert", message: "Please Enter Valid Class Name.")
        }else{
            let index = txtClassNumber.text!.startIndex.advancedBy(3)
            let res = containsOnlyLetters(txtClassNumber.text!.substringToIndex(index))
            let badCharacters = NSCharacterSet.decimalDigitCharacterSet().invertedSet
            
            if res == true && (txtClassNumber.text!.substringFromIndex(index)).rangeOfCharacterFromSet(badCharacters) == nil{
               print("correct")
                performSegueWithIdentifier("segueProffessor", sender: self)
            }else{
                self.simpleAlert("Alert", message: "Please Enter Valid Class Name.")
                
            }
        }
    }
    func textField(textField: UITextField, shouldChangeCharactersInRange range: NSRange, replacementString string: String) -> Bool {
        txtClassNumber.autocapitalizationType = UITextAutocapitalizationType.AllCharacters

        let str = (textField.text! as NSString).stringByReplacingCharactersInRange(range, withString: string.uppercaseString)
        let currentCharacterCount = str.characters.count ?? 0
        if currentCharacterCount >= 7{
            //let myNSString = str as NSString
            //myNSString.substringWithRange(NSRange(location: 0, length: 5))
        }else{
           textField.text = str
        }
        return false
    }
    func containsOnlyLetters(input: String) -> Bool {
        for chr in input.characters {
            if (!(chr >= "A" && chr <= "Z") ) {
                return false
            }
        }
        return true
    }
  
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        let nextVC = segue.destinationViewController as! ClassNameVC
        nextVC.passesName = txtClassNumber.text!
           // backItem.title = "Create New Class"
            //self.navigationItem.backBarButtonItem = backItem
        
    }
    override func touchesBegan(touches: Set<UITouch>, withEvent event: UIEvent?) {
        self.view.endEditing(true)
    }
    override func preferredStatusBarStyle() -> UIStatusBarStyle {
        return .LightContent
    }
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
}