export interface topupResponse{
    id : number
    paymentType : string
    amount  : number
    cardType:string
    cardCode :string
    cardSerial :string
    paymentStatus:string
    createdAt : Date
}