//
//  ParticipantsTableViewCell.swift
//  Classmates
//
//  Created by MAC on 7/28/16.
//  Copyright © 2016 Mallu. All rights reserved.
//

import Foundation
class ParticipantsTableViewCell: UITableViewCell{
    
    @IBOutlet weak var itemName: UILabel!
    @IBOutlet weak var itemImg: UIImageView!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        
        itemImg.layer.cornerRadius = itemImg.frame.size.width/2.0
        itemImg.clipsToBounds = true
    }
}