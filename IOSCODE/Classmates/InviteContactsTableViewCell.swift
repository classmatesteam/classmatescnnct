//
//  InviteContactsTableViewCell.swift
//  Classmates
//
//  Created by Mallu on 6/28/16.
//  Copyright © 2016 Mallu. All rights reserved.
//

import Foundation
class InviteContactsTableViewCell: UITableViewCell{
    
    @IBOutlet weak var itemName: UILabel!
    @IBOutlet weak var btnInvite: UIButton!
    @IBOutlet weak var itemImg: UIImageView!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        
        btnInvite.layer.cornerRadius = 7.0
        btnInvite.clipsToBounds = true
        btnInvite.layer.borderWidth = 1.0
        btnInvite.layer.borderColor = colorLightBlue.CGColor
        btnInvite.tintColor = colorLightBlue
        btnInvite.backgroundColor = UIColor.whiteColor()
      
        itemImg.layer.cornerRadius = itemImg.frame.size.width/2.0
        itemImg.clipsToBounds = true
    }
}