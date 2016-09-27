//
//  AcceptInviteTableViewCell.swift
//  Classmates
//
//  Created by MAC on 7/14/16.
//  Copyright © 2016 Mallu. All rights reserved.
//

import Foundation
class AcceptInviteTableViewCell: UITableViewCell{
    
    
    @IBOutlet weak var itemCourseName: UILabel!
    @IBOutlet weak var itemUserName: UILabel!
    @IBOutlet weak var itemUserImage: UIImageView!
    @IBOutlet weak var btnAccept: UIButton!
    @IBOutlet weak var btnReject: UIButton!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        btnAccept.layer.cornerRadius = 7.0
        btnAccept.clipsToBounds = true
        btnAccept.layer.borderWidth = 1.0
        btnAccept.layer.borderColor = colorLightBlue.CGColor
        btnAccept.tintColor = colorLightBlue
        btnAccept.backgroundColor = UIColor.whiteColor()
        btnReject.layer.cornerRadius = 7.0
        btnReject.clipsToBounds = true
        btnReject.layer.borderWidth = 1.0
        btnReject.layer.borderColor = colorLightBlue.CGColor
        btnReject.tintColor = colorLightBlue
        btnReject.backgroundColor = UIColor.whiteColor()
        itemUserImage.layer.cornerRadius = itemUserImage.frame.size.width/2.0
        itemUserImage.clipsToBounds = true
        itemUserName.adjustsFontSizeToFitWidth = true
        itemCourseName.adjustsFontSizeToFitWidth = true
    }
}