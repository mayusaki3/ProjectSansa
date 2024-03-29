
#pragma once

#include <AzCore/Component/Component.h>

#include <pong/pongBus.h>

namespace pong
{
    class pongSystemComponent
        : public AZ::Component
        , protected pongRequestBus::Handler
    {
    public:
        AZ_COMPONENT(pongSystemComponent, "{775af133-dc1f-4211-b07f-be0fcd2e16a0}");

        static void Reflect(AZ::ReflectContext* context);

        static void GetProvidedServices(AZ::ComponentDescriptor::DependencyArrayType& provided);
        static void GetIncompatibleServices(AZ::ComponentDescriptor::DependencyArrayType& incompatible);
        static void GetRequiredServices(AZ::ComponentDescriptor::DependencyArrayType& required);
        static void GetDependentServices(AZ::ComponentDescriptor::DependencyArrayType& dependent);

        pongSystemComponent();
        ~pongSystemComponent();

    protected:
        ////////////////////////////////////////////////////////////////////////
        // pongRequestBus interface implementation

        ////////////////////////////////////////////////////////////////////////

        ////////////////////////////////////////////////////////////////////////
        // AZ::Component interface implementation
        void Init() override;
        void Activate() override;
        void Deactivate() override;
        ////////////////////////////////////////////////////////////////////////
    };
}
