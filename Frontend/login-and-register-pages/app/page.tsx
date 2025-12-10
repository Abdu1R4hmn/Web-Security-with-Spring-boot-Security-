import Link from "next/link"
import { Button } from "@/components/ui/button"

export default function Home() {
  return (
    <main className="min-h-screen flex items-center justify-center bg-gradient-to-br from-background to-muted p-4">
      <div className="text-center space-y-6 max-w-md">
        <div className="space-y-2">
          <h1 className="text-4xl font-bold text-foreground">Welcome</h1>
          <p className="text-muted-foreground text-lg">Get started with your account</p>
        </div>

        <div className="space-y-3 pt-4">
          <Link href="/login" className="block">
            <Button size="lg" className="w-full">
              Login
            </Button>
          </Link>
          <Link href="/register" className="block">
            <Button size="lg" variant="outline" className="w-full bg-transparent">
              Register
            </Button>
          </Link>
        </div>

        <p className="text-sm text-muted-foreground pt-4">Choose an option above to continue</p>
      </div>
    </main>
  )
}
