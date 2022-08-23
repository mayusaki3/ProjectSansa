
#pragma once

#include <AzCore/EBus/EBus.h>
#include <AzCore/Interface/Interface.h>

namespace pong
{
    class pongRequests
    {
    public:
        AZ_RTTI(pongRequests, "{6feb512f-2ede-4dd5-821a-26659886b719}");
        virtual ~pongRequests() = default;
        // Put your public methods here
    };

    class pongBusTraits
        : public AZ::EBusTraits
    {
    public:
        //////////////////////////////////////////////////////////////////////////
        // EBusTraits overrides
        static constexpr AZ::EBusHandlerPolicy HandlerPolicy = AZ::EBusHandlerPolicy::Single;
        static constexpr AZ::EBusAddressPolicy AddressPolicy = AZ::EBusAddressPolicy::Single;
        //////////////////////////////////////////////////////////////////////////
    };

    using pongRequestBus = AZ::EBus<pongRequests, pongBusTraits>;
    using pongInterface = AZ::Interface<pongRequests>;

} // namespace pong
