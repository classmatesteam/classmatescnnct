//
//  MessagesTableViewCell.swift
//  Classmates
//
//  Created by Mallu on 6/22/16.
//  Copyright © 2016 Mallu. All rights reserved.
//

import Foundation
class MessagesTableViewCell: UITableViewCell{
    
    @IBOutlet weak var itemName: UILabel!
    @IBOutlet weak var itemUser: UILabel!
    @IBOutlet weak var itemImg: UIImageView!
    @IBOutlet weak var viewSide: UIView!
    
    override func awakeFromNib() {
        itemImg.layer.cornerRadius = itemImg.frame.size.width/2.0
        itemImg.clipsToBounds = true
    }
}