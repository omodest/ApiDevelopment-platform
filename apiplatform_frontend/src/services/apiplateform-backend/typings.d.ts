declare namespace API {
  type BaseResponse = {
    code?: number;
    data?: Record<string, any>;
    message?: string;
  };

  type BaseResponseBoolean_ = {
    code?: number;
    data?: boolean;
    message?: string;
  };

  type BaseResponseInt_ = {
    code?: number;
    data?: number;
    message?: string;
  };

  type BaseResponseInterfaceInfo_ = {
    code?: number;
    data?: InterfaceInfo;
    message?: string;
  };

  type BaseResponseListInterfaceInfo_ = {
    code?: number;
    data?: InterfaceInfo[];
    message?: string;
  };

  type BaseResponseListInterfaceInfoVO_ = {
    code?: number;
    data?: InterfaceInfoVO[];
    message?: string;
  };

  type BaseResponseListUserInterfaceInfo_ = {
    code?: number;
    data?: UserInterfaceInfo[];
    message?: string;
  };

  type BaseResponseLoginUserVO_ = {
    code?: number;
    data?: LoginUserVO;
    message?: string;
  };

  type BaseResponseLong_ = {
    code?: number;
    data?: number;
    message?: string;
  };

  type BaseResponseObject_ = {
    code?: number;
    data?: Record<string, any>;
    message?: string;
  };

  type BaseResponsePageInterfaceInfo_ = {
    code?: number;
    data?: PageInterfaceInfo_;
    message?: string;
  };

  type BaseResponsePagePost_ = {
    code?: number;
    data?: PagePost_;
    message?: string;
  };

  type BaseResponsePagePostVO_ = {
    code?: number;
    data?: PagePostVO_;
    message?: string;
  };

  type BaseResponsePageProductInfo_ = {
    code?: number;
    data?: PageProductInfo_;
    message?: string;
  };

  type BaseResponsePageUser_ = {
    code?: number;
    data?: PageUser_;
    message?: string;
  };

  type BaseResponsePageUserInterfaceInfo_ = {
    code?: number;
    data?: PageUserInterfaceInfo_;
    message?: string;
  };

  type BaseResponsePageUserVO_ = {
    code?: number;
    data?: PageUserVO_;
    message?: string;
  };

  type BaseResponsePostVO_ = {
    code?: number;
    data?: PostVO;
    message?: string;
  };

  type BaseResponseString_ = {
    code?: number;
    data?: string;
    message?: string;
  };

  type BaseResponseUser_ = {
    code?: number;
    data?: User;
    message?: string;
  };

  type BaseResponseUserInterfaceInfo_ = {
    code?: number;
    data?: UserInterfaceInfo;
    message?: string;
  };

  type BaseResponseUserVO_ = {
    code?: number;
    data?: UserVO;
    message?: string;
  };

  type checkUsingGETParams = {
    /** echostr */
    echostr?: string;
    /** nonce */
    nonce?: string;
    /** signature */
    signature?: string;
    /** timestamp */
    timestamp?: string;
  };

  type DeleteRequest = {
    id?: number;
  };

  type getCaptchaUsingGETParams = {
    /** emailAccount */
    emailAccount?: string;
  };

  type getInterfaceInfoVOByIdUsingGETParams = {
    /** id */
    id?: number;
  };

  type getPostVOByIdUsingGETParams = {
    /** id */
    id?: number;
  };

  type getUserByIdUsingGETParams = {
    /** id */
    id?: number;
  };

  type getUserInterfaceInfoVOByIdUsingGETParams = {
    /** id */
    id?: number;
  };

  type getUserVOByIdUsingGETParams = {
    /** id */
    id?: number;
  };

  type IdRequest = {
    id?: number;
  };

  type InterfaceInfo = {
    createTime?: string;
    id?: number;
    interfaceDescript?: string;
    interfaceName?: string;
    interfaceStatus?: number;
    interfaceType?: string;
    interfaceUrl?: string;
    is_deleted?: number;
    requestHeader?: string;
    requestParams?: string;
    responceHeader?: string;
    update_time?: string;
    userId?: number;
  };

  type InterfaceInfoAddRequest = {
    interfaceDescript?: string;
    interfaceName?: string;
    interfaceType?: string;
    interfaceUrl?: string;
    requestHeader?: string;
    requestParams?: string;
    responceHeader?: string;
  };

  type InterfaceInfoInvokeRequest = {
    id?: number;
    requestParams?: string;
  };

  type InterfaceInfoQueryRequest = {
    current?: number;
    id?: number;
    interfaceDescript?: string;
    interfaceName?: string;
    interfaceStatus?: number;
    interfaceType?: string;
    interfaceUrl?: string;
    pageSize?: number;
    requestHeader?: string;
    responceHeader?: string;
    sortField?: string;
    sortOrder?: string;
    userId?: number;
  };

  type InterfaceInfoUpdateRequest = {
    id?: number;
    interfaceDescript?: string;
    interfaceName?: string;
    interfaceStatus?: number;
    interfaceType?: string;
    interfaceUrl?: string;
    requestHeader?: string;
    requestParams?: string;
    responceHeader?: string;
  };

  type InterfaceInfoVO = {
    createTime?: string;
    id?: number;
    interfaceDescript?: string;
    interfaceName?: string;
    interfaceStatus?: number;
    interfaceType?: string;
    interfaceUrl?: string;
    is_deleted?: number;
    requestHeader?: string;
    requestParams?: string;
    responceHeader?: string;
    totalNum?: number;
    update_time?: string;
    userId?: number;
  };

  type listInterfaceInfoByPageUsingGETParams = {
    current?: number;
    id?: number;
    interfaceDescript?: string;
    interfaceName?: string;
    interfaceStatus?: number;
    interfaceType?: string;
    interfaceUrl?: string;
    pageSize?: number;
    requestHeader?: string;
    responceHeader?: string;
    sortField?: string;
    sortOrder?: string;
    userId?: number;
  };

  type listProductInfoByPageUsingGETParams = {
    addPoints?: number;
    current?: number;
    description?: string;
    name?: string;
    pageSize?: number;
    productType?: string;
    sortField?: string;
    sortOrder?: string;
    total?: number;
  };

  type listUserInterfaceInfoByPageUsingGET1Params = {
    current?: number;
    id?: number;
    interfaceInfoId?: number;
    leftNum?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    status?: number;
    totalNum?: number;
    userId?: number;
  };

  type listUserInterfaceInfoByPageUsingGETParams = {
    current?: number;
    id?: number;
    interfaceInfoId?: number;
    leftNum?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    status?: number;
    totalNum?: number;
    userId?: number;
  };

  type LoginUserVO = {
    age?: number;
    createTime?: string;
    id?: number;
    kunCoin?: number;
    qq?: string;
    sex?: string;
    telephone?: string;
    updateTime?: string;
    userAvatar?: string;
    userName?: string;
    userProfile?: string;
    userRole?: string;
  };

  type OrderItem = {
    asc?: boolean;
    column?: string;
  };

  type PageInterfaceInfo_ = {
    countId?: string;
    current?: number;
    maxLimit?: number;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: number;
    records?: InterfaceInfo[];
    searchCount?: boolean;
    size?: number;
    total?: number;
  };

  type PagePost_ = {
    countId?: string;
    current?: number;
    maxLimit?: number;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: number;
    records?: Post[];
    searchCount?: boolean;
    size?: number;
    total?: number;
  };

  type PagePostVO_ = {
    countId?: string;
    current?: number;
    maxLimit?: number;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: number;
    records?: PostVO[];
    searchCount?: boolean;
    size?: number;
    total?: number;
  };

  type PageProductInfo_ = {
    countId?: string;
    current?: number;
    maxLimit?: number;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: number;
    records?: ProductInfo[];
    searchCount?: boolean;
    size?: number;
    total?: number;
  };

  type PageUser_ = {
    countId?: string;
    current?: number;
    maxLimit?: number;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: number;
    records?: User[];
    searchCount?: boolean;
    size?: number;
    total?: number;
  };

  type PageUserInterfaceInfo_ = {
    countId?: string;
    current?: number;
    maxLimit?: number;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: number;
    records?: UserInterfaceInfo[];
    searchCount?: boolean;
    size?: number;
    total?: number;
  };

  type PageUserVO_ = {
    countId?: string;
    current?: number;
    maxLimit?: number;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: number;
    records?: UserVO[];
    searchCount?: boolean;
    size?: number;
    total?: number;
  };

  type Post = {
    content?: string;
    createTime?: string;
    favourNum?: number;
    id?: number;
    isDelete?: number;
    tags?: string;
    thumbNum?: number;
    title?: string;
    updateTime?: string;
    userId?: number;
  };

  type PostAddRequest = {
    content?: string;
    tags?: string[];
    title?: string;
  };

  type PostEditRequest = {
    content?: string;
    id?: number;
    tags?: string[];
    title?: string;
  };

  type PostFavourAddRequest = {
    postId?: number;
  };

  type PostFavourQueryRequest = {
    current?: number;
    pageSize?: number;
    postQueryRequest?: PostQueryRequest;
    sortField?: string;
    sortOrder?: string;
    userId?: number;
  };

  type PostQueryRequest = {
    content?: string;
    current?: number;
    favourUserId?: number;
    id?: number;
    notId?: number;
    orTags?: string[];
    pageSize?: number;
    searchText?: string;
    sortField?: string;
    sortOrder?: string;
    tags?: string[];
    title?: string;
    userId?: number;
  };

  type PostThumbAddRequest = {
    postId?: number;
  };

  type PostUpdateRequest = {
    content?: string;
    id?: number;
    tags?: string[];
    title?: string;
  };

  type PostVO = {
    content?: string;
    createTime?: string;
    favourNum?: number;
    hasFavour?: boolean;
    hasThumb?: boolean;
    id?: number;
    tagList?: string[];
    thumbNum?: number;
    title?: string;
    updateTime?: string;
    user?: UserVO;
    userId?: number;
  };

  type ProductInfo = {
    addPoints?: number;
    createTime?: string;
    description?: string;
    expirationTime?: string;
    id?: number;
    isDelete?: number;
    name?: string;
    productType?: string;
    status?: number;
    total?: number;
    updateTime?: string;
    userId?: number;
  };

  type uploadFileUsingPOSTParams = {
    biz?: string;
  };

  type User = {
    accessKey?: string;
    age?: number;
    createTime?: string;
    id?: number;
    isDelete?: number;
    kunCoin?: number;
    mpOpenId?: string;
    qq?: string;
    secretKey?: string;
    sex?: string;
    telephone?: string;
    unionId?: string;
    updateTime?: string;
    userAccount?: string;
    userAvatar?: string;
    userName?: string;
    userPassword?: string;
    userProfile?: string;
    userRole?: string;
  };

  type UserAddRequest = {
    age?: number;
    qq?: string;
    sex?: string;
    telephone?: string;
    userAccount?: string;
    userAvatar?: string;
    userName?: string;
    userRole?: string;
  };

  type UserEmailRegisterRequest = {
    captcha?: string;
    emailAccount?: string;
    invitationCode?: string;
    userName?: string;
  };

  type UserInterfaceInfo = {
    createTime?: string;
    id?: number;
    interfaceInfoId?: number;
    isDelete?: number;
    leftNum?: number;
    status?: number;
    totalNum?: number;
    updateTime?: string;
    userId?: number;
  };

  type UserInterfaceInfoAddRequest = {
    interfaceInfoId?: number;
    leftNum?: number;
    totalNum?: number;
    userId?: number;
  };

  type UserInterfaceInfoUpdateRequest = {
    id?: number;
    leftNum?: number;
    status?: number;
    totalNum?: number;
  };

  type UserLoginRequest = {
    userAccount?: string;
    userPassword?: string;
  };

  type UserQueryRequest = {
    age?: number;
    current?: number;
    id?: number;
    mpOpenId?: string;
    pageSize?: number;
    qq?: string;
    sex?: string;
    sortField?: string;
    sortOrder?: string;
    telephone?: string;
    unionId?: string;
    userName?: string;
    userProfile?: string;
    userRole?: string;
  };

  type UserRegisterRequest = {
    checkPassword?: string;
    userAccount?: string;
    userPassword?: string;
  };

  type UserUpdateMyRequest = {
    age?: number;
    qq?: string;
    sex?: string;
    telephone?: string;
    userAvatar?: string;
    userName?: string;
    userProfile?: string;
  };

  type UserUpdateRequest = {
    age?: number;
    id?: number;
    qq?: string;
    sex?: string;
    telephone?: string;
    userAvatar?: string;
    userName?: string;
    userProfile?: string;
    userRole?: string;
  };

  type UserVO = {
    createTime?: string;
    id?: number;
    userAvatar?: string;
    userName?: string;
    userProfile?: string;
    userRole?: string;
  };
}
