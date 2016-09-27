//
//  ClassName.swift
//  Classmates
//
//  Created by Mallu on 6/15/16.
//  Copyright © 2016 Mallu. All rights reserved.
//

import Foundation
import UIKit
import Alamofire
class ClassNameVC: UIViewController, UITextFieldDelegate{
    
    
    @IBOutlet weak var viewTop: UIView!
    @IBOutlet weak var viewBack: UIView!
    @IBOutlet weak var btnDone: ConfigureButton!
   
    
    @IBOutlet weak var lblHint: UILabel!
    
    @IBOutlet weak var lblHint22: UILabel!
    
    @IBOutlet weak var lblHint3: UILabel!
    @IBOutlet weak var txtProffessorName: UITextField!
    var passesName = ""
    //var userID = ""
    override func viewDidLoad() {
        super.viewDidLoad()
        
        configureDesign()
    }
    override func viewWillAppear(animated: Bool) {
        super.viewWillAppear(true)
        
        
    }
    func configureDesign(){
       // myMutableString = NSMutableAttributedString(string: lblHint.text!, attributes: [NSFontAttributeName:UIFont(name: "Ubuntu", size: 12.0)!])
        //myMutableString.addAttribute(NSForegroundColorAttributeName, value: colorBottom, range: NSRange(location:29,length:14))
        //lblHint.attributedText = myMutableString
        
        lblHint.sizeToFit()
        lblHint22.sizeToFit()
        lblHint3.sizeToFit()
        lblHint22.frame.origin.x = lblHint.frame.origin.x + lblHint.frame.size.width
        lblHint3.frame.origin.x = lblHint22.frame.origin.x + lblHint22.frame.size.width
        initAppearance(btnDone)
        //initAppearNavigation(viewTop)
        viewBack.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(ClassNameVC.actionBack)))
        txtProffessorName.attributedPlaceholder = NSAttributedString(string: "Proffessor Name", attributes: [NSForegroundColorAttributeName: UIColor.whiteColor()])
        userID = defaults.stringForKey("userID")!
        txtProffessorName.delegate = self
    }
    
    @IBAction func actionAddClass(sender: AnyObject) {
        counter = range(txtProffessorName)
        if counter.characters.count == 0 {
            self.simpleAlert("Alert", message: "Please Enter Proffessor Name.")
        }else{
            hitApi("AddValue")
        }
    }
    @IBAction func actionDone(sender: AnyObject) {
        done()
    }
    func textFieldShouldReturn(textField: UITextField) -> Bool {
        done()
        return true
    }
    func done(){
        counter = range(txtProffessorName)
        if counter.characters.count == 0 {
            self.simpleAlert("Alert", message: "Please Enter Proffessor Name.")
        }else{
            hitApi("doneValue")
        }
    }
    func hitApi(value:String){
        /*let data = [
            "userid" : userID,
            "name" : passesName,
            "pname" : txtProffessorName.text! as String,
            "Addclass" : ""
        ]
        print(data)*/
        self.showHUD()
        Alamofire.request(.POST, apiLink, parameters: [
            "userid" : userID,
            "name" : passesName,
            "pname" : txtProffessorName.text! as String,
            "Addclass" : ""
            ])
            .responseJSON {response in
                //print(response.result)
                if String(response.result) == "SUCCESS"{
                    
                    if let JSON = response.result.value {
                        print("JSON: \(JSON)")
                        apiStatus = JSON["status"] as! String
                        if apiStatus == "success" {
                            self.hideHUD()
                            if value == "AddValue"{
                                self.navigationController?.pushViewController(self.storyboard?.instantiateViewControllerWithIdentifier("ClassNumberVC") as! ClassNumberVC, animated: true)
                            }else if value == "doneValue"{
                                self.performSegueWithIdentifier("segueMain", sender: self)
                            }
                        }else{
                            self.hideHUD()
                            self.simpleAlert("Alert", message: "Class Name Already Exist. Please Try Different Class Name.")
                        }
                    }
                }else{
                    self.hideHUD()
                    self.simpleAlert("Network Error", message: "Please check your internet connection...")
                }
        }//end api

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